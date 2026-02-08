package MapMarks;

import basemod.abstracts.CustomSavable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapMarksSaver
   implements CustomSavable<ArrayList<String>>
{
   private static HashMap<Integer, HashMap<String, String>> perActData = new HashMap<>();

   public ArrayList<String> onSave() {
     return getAllDataAsList();
   }

   public void onLoad(ArrayList<String> paramArrayList) {
     if (paramArrayList != null) {
       for (String str : paramArrayList) {
         try {
           String[] arrayOfString = str.split(",");
           if (arrayOfString.length >= 4) {
             int i = Integer.parseInt(arrayOfString[0]);
             String str1 = arrayOfString[1] + "," + arrayOfString[2];
             String str2 = arrayOfString[3];
             ((HashMap<String, String>)perActData.computeIfAbsent(Integer.valueOf(i), paramInteger -> new HashMap<>())).put(str1, str2); continue;
           }  if (arrayOfString.length == 3) {

             String str1 = arrayOfString[0] + "," + arrayOfString[1];
             String str2 = arrayOfString[2];
             ((HashMap<String, String>)perActData.computeIfAbsent(Integer.valueOf(1), paramInteger -> new HashMap<>())).put(str1, str2);
           }
         } catch (Exception exception) {
           System.out.println("MapMarks: 解析存档行失败 - " + str);
         }
       }
     } else {
       perActData = new HashMap<>();
     }
   }

   public static ArrayList<String> getAllDataAsList() {
     ArrayList<String> arrayList = new ArrayList();
     for (Map.Entry<Integer, HashMap<String, String>> entry : perActData.entrySet()) {
       int i = ((Integer)entry.getKey()).intValue();
       for (Map.Entry<String, String> entry1 : ((HashMap<String, String>)entry.getValue()).entrySet()) {
         String str1 = (String)entry1.getKey();
         String str2 = (String)entry1.getValue();
         arrayList.add(i + "," + str1 + "," + str2);
       }
     }
     return arrayList;
   }

   public static void clearAllData() {
     perActData.clear();
   }

   public static void commitActData(int paramInt, ArrayList<String> paramArrayList) {
     HashMap<String, String> hashMap = new HashMap<>();
     for (String str : paramArrayList) {
       String[] arrayOfString = str.split(",");
       if (arrayOfString.length == 3) {
         hashMap.put(arrayOfString[0] + "," + arrayOfString[1], arrayOfString[2]);
       }
     }

     if (hashMap.isEmpty()) {
       perActData.remove(Integer.valueOf(paramInt));
     } else {
       perActData.put(Integer.valueOf(paramInt), hashMap);
     }
   }

   public static String getSavedColor(int paramInt1, int paramInt2, int paramInt3) {
     HashMap hashMap = perActData.get(Integer.valueOf(paramInt1));
     if (hashMap != null) {
       return (String)hashMap.get(paramInt2 + "," + paramInt3);
     }
     return null;
   }
}
