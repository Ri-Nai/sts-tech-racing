package MapMarks;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Properties;

public class MapMarksStorage
{
   private static SpireConfig config;
   private static final String KEY_DATA = "map_marks_data";

   public static void init() {
     try {
       Properties properties = new Properties();

       properties.setProperty("map_marks_data", "[]");

       config = new SpireConfig("MapMarks", "MapMarksConfig", properties);
     } catch (IOException iOException) {
       iOException.printStackTrace();
       System.out.println("MapMarks: 初始化 SpireConfig 失败!");
     }
   }

   public static void save() {
     if (config == null) {
       return;
     }
     ArrayList<String> arrayList = MapMarksSaver.getAllDataAsList();

     Gson gson = new Gson();
     String str = gson.toJson(arrayList);

     config.setString("map_marks_data", str);
     try {
       config.save();
     }
     catch (IOException iOException) {
       iOException.printStackTrace();
     }
   }

   public static ArrayList<String> load() {
     if (config == null) {
       init();
     }
     if (config == null) return new ArrayList<>();

     String str = config.getString("map_marks_data");
     if (str == null || str.isEmpty()) {
       return new ArrayList<>();
     }

     try {
       Gson gson = new Gson();
       Type type = (new TypeToken<ArrayList<String>>() {  }).getType();
       return (ArrayList<String>)gson.fromJson(str, type);
     } catch (Exception exception) {
       System.out.println("MapMarks: 解析 Config 数据失败，重置为空。");
       return new ArrayList<>();
     }
   }

   public static void delete() {
     if (config == null)
       return;
     config.setString("map_marks_data", "[]");
     try {
       config.save();
     } catch (IOException iOException) {
       iOException.printStackTrace();
     }
   }
}
