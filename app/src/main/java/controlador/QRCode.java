package controlador;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by Felipe on 20/08/2016.
 */
public class QRCode {

    public static final String urlQRCode = "http://www.appet.hol.es/index.php/EncontrarAnimal/";

    public QRCode(){}

    public static Bitmap gerarQRCode(String conteudo){
        Bitmap resultado = null;

        try {
            //Gera QRCode
            Dimension tamanho = new Dimension(150,150);
            QRCodeWriter gerador = new QRCodeWriter();
            BitMatrix matrix = gerador.encode(conteudo, BarcodeFormat.QR_CODE, tamanho.getWidth(), tamanho.getHeight());

            //Converte QRCode para bitmap.
            resultado = Bitmap.createBitmap(tamanho.getWidth(), tamanho.getHeight(), Bitmap.Config.ARGB_8888);
            for (int i = 0; i < tamanho.getWidth(); i++) {
                for (int j = 0; j < tamanho.getHeight(); j++) {
                    resultado.setPixel(i, j, matrix.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
        }

        return resultado;
    }
}
