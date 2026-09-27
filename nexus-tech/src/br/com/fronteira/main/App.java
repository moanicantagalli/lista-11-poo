package br.com.fronteira.main;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class App {
    public static void main(String[] args) throws Exception {
        String encryptedB64 = "COLE_AQUI_A_STRING_CIFRADA_DO_SEU_PDF";
        encryptedB64 = encryptedB64.replaceAll("\\s", "");

        byte[] todosOsBytes = Base64.getDecoder().decode(encryptedB64);
        byte[] salt = Arrays.copyOfRange(todosOsBytes, 8, 16);
        byte[] textoCifrado = Arrays.copyOfRange(todosOsBytes, 16, todosOsBytes.length);

        String charset = "abcdefghijklmnopqrstuvwxyz0123456789";
        System.out.println("Iniciando ataque de forca bruta no link da NexusTech...");
        long startTime = System.currentTimeMillis();

        for (char c1 : charset.toCharArray()) {
            for (char c2 : charset.toCharArray()) {
                String testPass = "jav" + c1 + c2;
                try {
                    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                    PBEKeySpec spec = new PBEKeySpec(testPass.toCharArray(), salt, 1000, 48 * 8);
                    byte[] keyAndIv = factory.generateSecret(spec).getEncoded();

                    byte[] key = Arrays.copyOfRange(keyAndIv, 0, 32);
                    byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);

                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
                    byte[] resultBytes = cipher.doFinal(textoCifrado);
                    String result = new String(resultBytes);

                    if (result.contains("http")) {
                        long endTime = System.currentTimeMillis();
                        System.out.println("\nSUCESSO! A criptografia foi quebrada!");
                        System.out.println("Senha encontrada: " + testPass);
                        System.out.println("Link revelado: " + result.trim());
                        System.out.println("Tempo de execucao: " + (endTime - startTime) + "ms");
                        return;
                    }
                } catch (Exception e) {
                    // Senha incorreta: falha de padding, ignora e testa a proxima
                }
            }
        }
        System.out.println("\nForca bruta concluida. Senha nao encontrada.");
    }
}
