package org.mn.dropzone.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mn.dropzone.crypto.Crypto;
import org.mn.dropzone.crypto.CryptoException;
import org.mn.dropzone.crypto.CryptoSystemException;
import org.mn.dropzone.crypto.CryptoUtils;
import org.mn.dropzone.crypto.FileEncryptionCipher;
import org.mn.dropzone.crypto.InvalidFileKeyException;
import org.mn.dropzone.crypto.InvalidKeyPairException;
import org.mn.dropzone.crypto.model.EncryptedDataContainer;
import org.mn.dropzone.crypto.model.EncryptedFileKey;
import org.mn.dropzone.crypto.model.PlainDataContainer;
import org.mn.dropzone.crypto.model.PlainFileKey;
import org.mn.dropzone.crypto.model.UserPublicKey;
import org.mn.dropzone.eventlistener.UploadEvent;
import org.mn.dropzone.rest.RestClient.Status;
import org.mn.dropzone.rest.model.FileKeyContainer;
import org.mn.dropzone.rest.model.UserPublicKeyContainer;

public class CryptoUtil {

	private final String characterEncoding = "UTF-8";
	private final String cipherTransformation = "AES/CBC/PKCS5Padding";
	private final String aesEncryptionAlgorithm = "AES";
	private final String KEY_PREFIX = "PREFIX";
	private static final int BLOCK_SIZE = 16;

	public CryptoUtil() {
		Security.addProvider(new BouncyCastleProvider());
	}

	private byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
		cipherText = cipher.doFinal(cipherText);
		return cipherText;
	}

	private byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);
		return plainText;
	}

	private byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
		byte[] keyBytes = new byte[16];
		byte[] parameterKeyBytes = key.getBytes(characterEncoding);
		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}

	public String encrypt(String plainText, String key) {
		String encodedString = "";

		try {
			byte[] plainTextbytes = plainText.getBytes(characterEncoding);
			byte[] keyBytes = getKeyBytes(KEY_PREFIX + "" + key);
			encodedString = Base64.getEncoder().encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes));
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encodedString;

	}

	public String decrypt(String encryptedText, String key) {
		String decodedString = "";
		try {
			byte[] cipheredBytes = Base64.getDecoder().decode(encryptedText);

			byte[] keyBytes = getKeyBytes(KEY_PREFIX + "" + key);
			decodedString = new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
		} catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decodedString;

	}

	/**
	 * Encrypts the given file and returns the fileKey
	 *
	 * @param token
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File encryptFile(File file, PlainFileKey fileKey) throws IOException {

		// Encrypt blocks
		try {
			file = CryptoUtil.encryptData(fileKey, file);
		} catch (Exception e) {
			throw new IOException();
		}
		return file;
	}

	/**
	 * Encrypt fileKey with user's public key
	 * 
	 * @param fileKey
	 * @return
	 */
	public static FileKeyContainer encryptFileKey(PlainFileKey fileKey, UserPublicKeyContainer userPublicKeyContainer)
			throws IOException {
		FileKeyContainer fileKeyContainer = new FileKeyContainer();

		UserPublicKey userPublicKey = new UserPublicKey();
		userPublicKey.setPublicKey(userPublicKeyContainer.publicKey);
		userPublicKey.setVersion(userPublicKeyContainer.version);

		// encrypt fileKey
		try {
			EncryptedFileKey encFileKey = Crypto.encryptFileKey(fileKey, userPublicKey);
			fileKeyContainer = new FileKeyContainer();
			fileKeyContainer.iv = encFileKey.getIv();
			fileKeyContainer.key = encFileKey.getKey();
			fileKeyContainer.tag = encFileKey.getTag();
			fileKeyContainer.version = encFileKey.getVersion();
		} catch (InvalidFileKeyException | InvalidKeyPairException | CryptoSystemException e) {
			throw new IOException();
		}
		return fileKeyContainer;
	}

	/**
	 * Encrypts some bytes.
	 *
	 * @param fileKey
	 *            The file key to use.
	 * @param inputFile
	 *            The file that is to be encrypted
	 *
	 * @return Encrypted bytes.
	 *
	 * @throws Exception
	 */
	public static File encryptData(PlainFileKey fileKey, File inputFile) throws Exception {

		// !!! This method is an example for encryption. It uses byte array
		// streams for input and
		// output. However, any kind of stream (e.g. FileInputStream) could be
		// used here.

		FileEncryptionCipher cipher = Crypto.createFileEncryptionCipher(fileKey);

		FileInputStream is = new FileInputStream(inputFile);
		File outputFile = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + inputFile.getName());
		if (outputFile != null && outputFile.exists()) {
			outputFile.delete();
		}
		FileOutputStream os = new FileOutputStream(outputFile);

		byte[] buffer = new byte[BLOCK_SIZE];
		int count;

		try {
			EncryptedDataContainer eDataContainer;

			// Encrypt blocks
			while ((count = is.read(buffer)) != -1) {
				byte[] pData = createByteArray(buffer, count);
				eDataContainer = cipher.processBytes(new PlainDataContainer(pData));
				os.write(eDataContainer.getContent());
			}

			// Complete encryption
			eDataContainer = cipher.doFinal();
			os.write(eDataContainer.getContent());
			String tag = CryptoUtils.byteArrayToString(eDataContainer.getTag());
			fileKey.setTag(tag);

		} catch (IOException e) {
			throw new Exception("Error while reading/writing data!", e);
		} catch (CryptoException e) {
			throw new Exception("Error while encrypting data!", e);
		} finally {
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				// Nothing to do here
			}
		}

		return outputFile;
	}

	private static byte[] createByteArray(byte[] bytes, int len) {
		byte[] b = new byte[len];
		System.arraycopy(bytes, 0, b, 0, len);
		return b;
	}
}