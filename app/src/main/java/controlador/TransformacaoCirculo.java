package controlador;

/**
 * Created by Felipe on 21/08/2016.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class TransformacaoCirculo implements Transformation {

    @Override
    public Bitmap transform(Bitmap fonte) {
        int tamanho = Math.min(fonte.getWidth(), fonte.getHeight());

        int x = (fonte.getWidth() - tamanho) / 2;
        int y = (fonte.getHeight() - tamanho) / 2;

        Bitmap imgemQuadrada = Bitmap.createBitmap(fonte, x, y, tamanho, tamanho);
        if (imgemQuadrada != fonte) {
            fonte.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(tamanho, tamanho, fonte.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(imgemQuadrada, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = tamanho / 2f;
        canvas.drawCircle(r, r, r, paint);

        imgemQuadrada.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
