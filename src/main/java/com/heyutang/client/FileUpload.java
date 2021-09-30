package com.heyutang.client;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author heBao
 */
public class FileUpload {

    public void upload(Socket socket, String path) {
        DataOutputStream dos = null;
        try (
                FileInputStream fis = new FileInputStream(path)
        ) {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(path.split("\\.")[1]);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, len);
            }
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
