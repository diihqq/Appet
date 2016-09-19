package controlador;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Felipe on 18/09/2016.
 */
public class Imagem {
    public static String fotoEncode(String imagem){
        String resultado = "";

        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inSampleSize = 2;
        opcoes.inScaled = false;
        opcoes.inDither = false;
        opcoes.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmp = BitmapFactory.decodeFile(imagem,opcoes);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] fotoBytes = bytes.toByteArray();

        resultado = Base64.encodeToString(fotoBytes, Base64.DEFAULT);

        return resultado;
    }

    public static String recuperaCaminho(Uri arquivo, Context contexto){
        String resultado;
        Cursor cursor = contexto.getContentResolver().query(arquivo, null, null, null, null);
        if (cursor == null) {
            resultado = arquivo.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            resultado = cursor.getString(idx);
            cursor.close();
        }
        return resultado;
    }
}
