package hu.bute.auctionapp.parsewrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hu.bute.auctionapp.data.ProductData;
import hu.bute.auctionapp.data.StoreData;
import hu.bute.auctionapp.data.UserData;

/**
 * A Parse backend-szolgáltatást használó cloud handler.
 * Created by Varsi on 2014.12.04..
 */
public class ParseHandler implements CloudHandler {
    private static final String USER_USERNAME = "username";
    private static final String USER_PASSWORD = "password";
    private static final String USER_CLASSNAME = "AuctionUser";
    private static final String STORE_CLASSNAME = "Stores";
    private static final String STORE_NAME = "name";
    private static final String STORE_LOGO_PICTRUE = "logo_pic";
    private static final String STORE_CLICKS = "clicks";
    private static final String STORE_TYPE = "type";
    private static final String PRODUCT_CLASSNAME = "Products";
    private static final String PRODUCT_NAME = "name";
    private static final String PRODUCT_PRICE = "price";
    private static final String PRODUCT_STOREID = "storeId";
    private static final String PRODUCT_DURATION_END = "duration_end";
    private static final String PRODUCT_PROPERTIES = "properties";
    private static final String PRODUCT_COMMENTS = "comments";
    private static final String PRODUCT_GPS_LAT = "gps_lat";
    private static final String PRODUCT_GPS_LON = "gps_lon";
    private static final String PRODUCT_ADDRESS = "address";
    private static final String PRODUCT_PICTURE = "picture";
    private static final String PRODUCT_CURRENCTY = "currency";
    private static final String PRODUCT_CLICKS = "clicks";
    private static final String PRODUCT_CATEGORY = "category";
    private Context context;

    public ParseHandler(Context context) {
        this.context = context;
    }

    public static String MD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    private static UserData parseObjectToUser(ParseObject obj) {
        if (obj == null)
            return null;
        String userName = obj.getString(USER_USERNAME);
        String passwordMD5 = obj.getString(USER_PASSWORD);
        UserData result = new UserData(userName, passwordMD5);
        result.setObjectId(obj.getObjectId());
        return result;
    }

    private StoreData parseObjectToStoreDirectly(ParseObject obj, boolean fileSave) {
        String name = obj.getString(STORE_NAME);
        Number clicknum = obj.getNumber(STORE_CLICKS);
        int clicks = clicknum == null ? 0 : clicknum.intValue();
        String storeType = obj.getString(STORE_TYPE);
        final StoreData result = new StoreData(name, clicks, storeType);
        result.setObjectId(obj.getObjectId());

        final ParseFile picture = obj.getParseFile(STORE_LOGO_PICTRUE);
        if (picture == null || !fileSave) {
            return result;
        }
        File f = context.getFileStreamPath(picture.getName());
        Date lastModified = obj.getUpdatedAt();
        Date fileLastModified = new Date(f.lastModified());
        if (f.exists() && fileLastModified.compareTo(lastModified) > 0) {
            result.setPictureFileName(picture.getName());
        } else {
            try {
                byte[] bytes = picture.getData();
                String fileName = picture.getName();
                saveToFile(fileName, bytes);
                result.setPictureFileName(fileName);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void parseObjectToStore(ParseObject obj, boolean fileSave, final ResultCallback callback) {
        String name = obj.getString(STORE_NAME);
        Number clicknum = obj.getNumber(STORE_CLICKS);
        int clicks = clicknum == null ? 0 : clicknum.intValue();
        String storeType = obj.getString(STORE_TYPE);
        final StoreData result = new StoreData(name, clicks, storeType);
        result.setObjectId(obj.getObjectId());

        final ParseFile picture = obj.getParseFile(STORE_LOGO_PICTRUE);
        if (picture == null || !fileSave) {
            callback.onResult(result);
            return;
        }
        File f = context.getFileStreamPath(picture.getName());
        Date lastModified = obj.getUpdatedAt();
        Date fileLastModified = new Date(f.lastModified());
        if (f.exists() && fileLastModified.compareTo(lastModified) > 0) {
            result.setPictureFileName(picture.getName());
            callback.onResult(result);
        } else {
            picture.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        String fileName = picture.getName();
                        saveToFile(fileName, bytes);
                        result.setPictureFileName(fileName);
                        callback.onResult(result);
                    }
                }
            });
        }
    }

    @Override
    public List<ProductData> getProductsByViewDirectly(int skip, int limit, String filter) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PRODUCT_CLASSNAME);
        query.setSkip(skip);
        query.setLimit(limit);
        query.orderByDescending(PRODUCT_CLICKS);
        if (filter != null) {
            query.whereEqualTo(PRODUCT_CATEGORY, filter);
        }
        final List<ProductData> result = new ArrayList<ProductData>();
        try {
            List<ParseObject> parseObjects = query.find();
            for (ParseObject obj : parseObjects) {
                ProductData product = null;
                try {
                    product = parseObjectToProductDirectly(obj);
                    result.add(product);
                } catch (StoreNotFoundException e) {
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ProductData parseObjectToProductDirectly(ParseObject obj) throws StoreNotFoundException {
        ParseObject store = obj.getParseObject(PRODUCT_STOREID);
        try {
            store.fetch();
            StoreData storeData = parseObjectToStoreDirectly(store, false);
            ProductData result = new ProductData(obj.getString(PRODUCT_NAME), storeData, obj.getDouble(PRODUCT_PRICE), obj.getDate(PRODUCT_DURATION_END));
            result.setAddress(obj.getString(PRODUCT_ADDRESS));
            result.setGpsLat(obj.getDouble(PRODUCT_GPS_LAT));
            result.setGpsLon(obj.getDouble(PRODUCT_GPS_LON));
            result.setComment(obj.getString(PRODUCT_COMMENTS));
            result.setProperties(obj.getString(PRODUCT_PROPERTIES));
            result.setCurrency(obj.getString(PRODUCT_CURRENCTY));
            result.setClicks(obj.getInt(PRODUCT_CLICKS));
            result.setCategory(obj.getString(PRODUCT_CATEGORY));
            ParseFile picture = obj.getParseFile(PRODUCT_PICTURE);
            result.setObjectId(obj.getObjectId());
            try {
                if (picture != null) {
                    byte[] bytes = picture.getData();
                    String fileName = picture.getName();
                    saveToFile(fileName, bytes);
                    result.setPictureFileName(fileName);
                }
            } catch (ParseException e) {
                result.setPictureFileName(null);
            }
            return result;
        } catch (ParseException e) {
            throw new StoreNotFoundException();
        }
    }

    @Override
    public List<ProductData> getProdcutsByLastChangedDirectly(int skip, int limit, String filter) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PRODUCT_CLASSNAME);
        query.setSkip(skip);
        query.setLimit(limit);
        query.orderByDescending("updatedAt");
        if (filter != null) {
            query.whereEqualTo(PRODUCT_CATEGORY, filter);
        }
        final List<ProductData> result = new ArrayList<ProductData>();
        try {
            List<ParseObject> parseObjects = query.find();
            for (ParseObject obj : parseObjects) {
                ProductData product = null;
                try {
                    product = parseObjectToProductDirectly(obj);
                    result.add(product);
                } catch (StoreNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void getUser(String objectid, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        query.getInBackground(objectid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    callback.onResult(parseObjectToUser(result));
                } else {
                    callback.onResult(null);
                }
            }
        });
    }

    @Override
    public void getUser(final String username, final String password, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        final String passwordMD5 = MD5(password);
        query.whereEqualTo(USER_USERNAME, username);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                System.out.println(e);
                if (e != null || parseObject == null) {
                    callback.onResult(null);
                } else {
                    if (parseObject.getString(USER_PASSWORD).equals(passwordMD5)) {
                        callback.onResult(parseObjectToUser(parseObject));
                    } else {
                        callback.onResult(null);
                    }
                }
            }
        });
    }

    @Override
    public UserData getUserDirectly(String username, String passmd5) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
        query.whereEqualTo(USER_USERNAME, username);
        query.whereEqualTo(USER_PASSWORD, passmd5);
        try {
            ParseObject first = query.getFirst();
            return parseObjectToUser(first);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveUser(final UserData userData, final ResultCallback callback) {
        if (userData.getObjectId() == null) {
            //regisztráció
            ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
            query.whereEqualTo(USER_USERNAME, userData.getName());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject == null) {
                        final ParseObject obj = ParseObject.create(USER_CLASSNAME);
                        final String passwordMD5 = MD5(userData.getPasswordMD5());
                        obj.put(USER_USERNAME, userData.getName());
                        obj.put(USER_PASSWORD, passwordMD5);
                        obj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    callback.onResult(parseObjectToUser(obj));
                                } else {
                                    callback.onResult(null);
                                }
                            }
                        });
                    } else {
                        callback.onResult(null);
                    }
                }
            });
        } else {
            //módosítás
            ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASSNAME);
            query.getInBackground(userData.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject == null) {
                        callback.onResult(null);
                    } else {
                        parseObject.put(USER_USERNAME, userData.getName());
                        parseObject.put(USER_PASSWORD, userData.getPasswordMD5());
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                callback.onResult(userData);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void getStore(String objectid, final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
        query.getInBackground(objectid, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null) {
                    parseObjectToStore(parseObject, true, callback);
                } else {
                    callback.onResult(null);
                }
            }
        });
    }

    @Override
    public void getStores(final ResultCallback callback) {
    }

    private void saveToFile(String fileName, byte[] bytes) {
        FileOutputStream fos = null;
        System.out.println("saveToFile: " + fileName);
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(bytes, 0, bytes.length);
        } catch (FileNotFoundException e1) {
        } catch (IOException e1) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    @Override
    public void saveStore(final StoreData data, final ResultCallback callback) {
        if (data.getObjectId() == null) {
            //feltöltés
            ParseObject storeObj = ParseObject.create(STORE_CLASSNAME);
            saveStore(data, storeObj, callback);
        } else {
            //módosítás, egyelőre csak a nézettséget frissíti
            ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
            query.getInBackground(data.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject parseObject, ParseException e) {
                    if (e == null && parseObject != null) {
                        parseObject.put(STORE_CLICKS, data.getClicks());
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                callback.onResult(e == null);
                            }
                        });
                    } else {
                        callback.onResult(false);
                    }
                }
            });
        }
    }

    @Override
    public List<StoreData> getStoresByViewDirectly(int skip, int limit, String filter) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
        query.setSkip(skip);
        query.setLimit(limit);
        query.orderByDescending(STORE_CLICKS);
        if (filter != null) {
            query.whereEqualTo(STORE_TYPE, filter);
        }
        final List<StoreData> result = new ArrayList<StoreData>();
        try {
            List<ParseObject> parseObjects = query.find();
            for (ParseObject obj : parseObjects) {
                StoreData store = parseObjectToStoreDirectly(obj, true);
                result.add(store);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<StoreData> getStoresByLastChangedDirectly(int skip, int limit, String filter) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
        query.setSkip(skip);
        query.setLimit(limit);
        query.orderByDescending("updatedAt");
        if (filter != null) {
            query.whereEqualTo(STORE_TYPE, filter);
        }
        final List<StoreData> result = new ArrayList<StoreData>();
        try {
            List<ParseObject> parseObjects = query.find();
            for (ParseObject obj : parseObjects) {
                StoreData store = parseObjectToStoreDirectly(obj, true);
                result.add(store);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveStore(StoreData data, final ParseObject storeObj, final ResultCallback callback) {
        storeObj.put(STORE_NAME, data.getName());
        storeObj.put(STORE_CLICKS, data.getClicks());
        storeObj.put(STORE_TYPE, data.getType());
        if (data.getPictureFileName() != null) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(data.getPictureFileName(), opt);
            int imgWidth = opt.outWidth;

            int realWidth = 128;
            int scaleFactor = Math.round((float) imgWidth / (float) realWidth);
            opt.inSampleSize = scaleFactor;
            opt.inJustDecodeBounds = false;

            Bitmap img = BitmapFactory.decodeFile(data.getPictureFileName(), opt);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            final ParseFile picture = new ParseFile(baos.toByteArray());
            picture.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        storeObj.put(STORE_LOGO_PICTRUE, picture);
                        storeObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    callback.onResult(true);
                                } else {
                                    callback.onResult(false);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            storeObj.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    callback.onResult(e == null);
                }
            });
        }
    }

    @Override
    public void getProduct(String objectid, ResultCallback callback) {

    }

    @Override
    public void saveProduct(final ProductData data, final ResultCallback callback) {
        if (data.getObjectId() == null) {
            final ParseObject obj = ParseObject.create(PRODUCT_CLASSNAME);
            obj.put(PRODUCT_NAME, data.getName());
            obj.put(PRODUCT_PRICE, data.getPrice());
            obj.put(PRODUCT_ADDRESS, data.getAddress());
            obj.put(PRODUCT_GPS_LAT, data.getGpsLat());
            obj.put(PRODUCT_GPS_LON, data.getGpsLon());
            obj.put(PRODUCT_COMMENTS, data.getComment());
            obj.put(PRODUCT_PROPERTIES, data.getProperties());
            obj.put(PRODUCT_DURATION_END, data.getDurationEnd());
            obj.put(PRODUCT_CURRENCTY, data.getCurrency());
            obj.put(PRODUCT_CLICKS, data.getClicks());
            obj.put(PRODUCT_CATEGORY, data.getCategory());
            ParseQuery<ParseObject> storeQuery = ParseQuery.getQuery(STORE_CLASSNAME);
            storeQuery.getInBackground(data.getStore().getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject result, ParseException e) {
                    if (e == null) {
                        obj.put(PRODUCT_STOREID, result);
                        if (data.getPictureFileName() != null) {
                            BitmapFactory.Options opt = new BitmapFactory.Options();
                            opt.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(data.getPictureFileName(), opt);
                            int imgWidth = opt.outWidth;

                            int realWidth = 512;
                            int scaleFactor = Math.round((float) imgWidth / (float) realWidth);
                            opt.inSampleSize = scaleFactor;
                            opt.inJustDecodeBounds = false;

                            Bitmap img = BitmapFactory.decodeFile(data.getPictureFileName(), opt);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            final ParseFile picture = new ParseFile(baos.toByteArray());
                            picture.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        obj.put(PRODUCT_PICTURE, picture);
                                        obj.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    callback.onResult(true);
                                                } else {
                                                    callback.onResult(false);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            obj.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    callback.onResult(e == null);
                                }
                            });
                        }
                    }
                }
            });
        } else {
            //módosítás
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(PRODUCT_CLASSNAME);
            query.getInBackground(data.getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    parseObject.put(PRODUCT_CLICKS, data.getClicks());
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            callback.onResult(e == null);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void getStoresWithoutImages(final ResultCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
        query.orderByAscending(STORE_NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    final List<StoreData> result = new ArrayList<StoreData>();
                    final int max = parseObjects.size();
                    final int[] counter = {0};
                    for (ParseObject obj : parseObjects) {
                        parseObjectToStore(obj, false, new ResultCallback() {
                            @Override
                            public void onResult(Object data) {
                                result.add((StoreData) data);
                                counter[0] = counter[0] + 1;
                                if (counter[0] == max) {
                                    callback.onResult(result);
                                }
                            }
                        });
                    }
                } else {
                    callback.onResult(new ArrayList<StoreData>());
                }
            }
        });
    }

    @Override
    public List<StoreData> getMostPopularStoreDirectly(String category, int count) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_CLASSNAME);
        query.orderByDescending(STORE_CLICKS);
        query.whereEqualTo(STORE_TYPE, category);
        query.setLimit(count);
        List<StoreData> result = new ArrayList<>();
        try {
            List<ParseObject> obj = query.find();
            for (ParseObject po : obj) {
                StoreData store = parseObjectToStoreDirectly(po, true);
                result.add(store);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private class StoreNotFoundException extends Exception {

    }
}