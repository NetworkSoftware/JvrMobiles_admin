package pro.network.jvradmin.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Appconfig {

    //Key values
    public static final String shopIdKey = "shopIdKey";
    public static final String mypreference = "mypref";

    public static final String ip = "http://thestockbazaar.com/prisma/jvrmobiles";

   // public static final String ip = "http://192.168.1.100:8114/prisma/jvrmobiles";
    public static final int MAX_COUNT = 5;
    public static final int MAX_BANNER_COUNT = 1;
     //AD
    public static String URL_IMAGE_UPLOAD = ip + "/fileUpload";
    public static String AD_CREATE = ip + "/fileFeed";
    public static String AD_GET_ALL = ip + "/get_all_feed";
    public static String AD_DELETE = ip + "/fileDelete";

    //Stack
    public static final String PRODUCT_CREATE = ip + "/create_stock";
    public static final String PRODUCT_UPDATE = ip + "/update_stock";
    public static final String PRODUCT_GET_ALL = ip + "/dataFetchAll";
    public static final String PRODUCT_DELETE = ip + "/delete_stock";
    public static final String PRODUCT_GET_ID = ip + "/dataFetch_by_id";

    //Banner
    public static final String BANNERS_CREATE = ip + "/create_banner";
    public static final String BANNERS_UPDATE = ip + "/update_stock";
    public static final String BANNERS_GET_ALL = ip + "/dataFetchAll_banner";
    public static final String BANNERS_DELETE = ip + "/delete_banner";
    //Order
    public static final String ORDER_GET_ALL = ip + "/dataFetchAll_order";
    public static final String ORDER_CHANGE_STATUS = ip + "/order_change_status";
    //Feedback8
    public static final String FEEDBACK_GET_ALL = ip + "/dataFetchAll_feedback";

    public static final String IMAGE_URL = ip + "/images/";

    public static String getResizedImage(String path, boolean isResized) {
        if (isResized) {
            return IMAGE_URL + "small/" + path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }


    public static String compressImage(String filePath) {

        //String filePath = getRealPathFromURI(imageUri, context);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.

        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 2000.0f;
        float maxWidth = 2000.0f;

//        float imgRatio = actualWidth / actualHeight;
//        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {

            if (actualWidth > actualHeight) {
                float tempRatio = maxWidth / actualWidth;
                actualHeight = (int) (tempRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else if (actualWidth < actualHeight) {
                float tempRatio = maxHeight / actualHeight;
                actualWidth = (int) (tempRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }

//            if (imgRatio < maxRatio) {
//                imgRatio = maxHeight / actualHeight;
//                actualWidth = (int) (imgRatio * actualWidth);
//                actualHeight = (int) maxHeight;
//            } else if (imgRatio > maxRatio) {
//                imgRatio = maxWidth / actualWidth;
//                actualHeight = (int) (imgRatio * actualHeight);
//                actualWidth = (int) maxWidth;
//            } else {
//                actualHeight = (int) maxHeight;
//                actualWidth = (int) maxWidth;
//
//            }

        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String convertTimeToLocal(String time) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(time);
            df.setTimeZone(TimeZone.getDefault());
            return df.format(date);
        } catch (Exception e) {
            return time;
        }
    }

    public static boolean isDeviceSupportCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static String[] BRAND = new String[]{
            "IPHONE",
            "SAMSUNG",
            "ONE PLUS",
            "VIVO",
            "OPPO",
            "BLACKBERRY",
            "GOOGLE PIXEL",
            "REALME",
            "LG",
            "MI",
            "HONOR",
            "NOKIA",
            "SONY",
            "MOTOROLA",
            "HUAWEI",
            "LENOVA",
            "ASUS",
            "GIONEE",
            "BLACK SHARK",
            "JIO",
            "LYF",
            "NUBIA",
            "PANASONIC",
            "POCO",
            "REDMI",
            "VERTU",
            "COOLPAD",
            "KARBONN",
            "MICROSOFT",
            "MICROMAX",};

    public static Map<String, String[]> stringMap = new HashMap<String, String[]>() {{
        put("New Mobiles", BRAND);
        put("Old Mobiles", BRAND);
        put("Home Theatre", BRAND);
        put("Tablet", BRAND);
        put("Smart watches", new String[]{"IPHONE",
                "SAMSUNG",
                "VIVO",
                "OPPO",});
        put("Headphones", new String[]{
                "APPLE",
                "SAMSUNG ",
                "ONE PLUS",
                "JBL",
                "REALME",
                "SONY",
                "LG",
                "BOSE",
                "JVC",
                "OPPO ",
                "VIVO ",
                "BOAT",
                "AKG",
                "PIONEER",
                "HYPER X",
                "XIAOMI",
                "CREATIVE",
                "BEATS",
                "MONSTER",
                "JABRA",
                "WESTONE",
                "SKULLCANDY",
                "SENNHEISER",
                "PIONEER",
                "UBON",
                "HUAWEI",
                "NOISE",
        });
        put("Laptop", new String[]{"APPLE MACBOOK",
                "LENOVO ",
                "HP",
                "DELL",
                "SONY",
                "ASUS",
                "HUAWEI ",
                "ACER",
                "SAMSUNG ",
                "ACER",
                "TOSHIBA",
                "LG",
                "MSI",
                "MICROSOFT ",
                "WIPRO",
                "RAZER",
                "VAIO",
        });
        put("TV", new String[]{"SAMSUNG Tv",
                "SONY TV",
                "LG TV",
                "XIAOMI MI TV",
                "TCL TV",
                "BPL TV",
                "HAIER TV",
                "PANASONIC TV",
                "ONIDA TV",
                "Vu TV",
                "VIDEOCON TV",
                "MICROMAX TV",
                "INTEX TV",
                "SANSUI TV",
                "SANYO TV",
                "TOSHIBA TV",
                "MITASHI TV",


        });

        put("AC", new String[]{"SAMSUNG ",
                "LG",
                "WHIRLPOOL",
                "IFB",
                "VOLTAS",
                "DAIKIN",
                "BLUESTAR",
                "HITACHI",
                "O GENERAL",
                "CARRIER",
                "HAIER",
                "GODREJ",
                "KENSTAR",
                "SANYO",
                "PANASONIC ",
                "SHARP",
                "SAMSUI",
                "BOSCH",
                "TOSHIBA",
        });



        put("Service", new String[]{"PHONES ", "LAPTOPS ",});


    }};

    public static String[] CATEGORY = new String[]{
            "New Mobiles", "Old Mobiles", "Tablet", "Smart watches", "Headphones", "Laptop", "TV", "AC","Home Theatre","Service",
    };

    public static DefaultRetryPolicy getPolicy() {
        return new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

}
