package common;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        DataProcessing dp = DataProcessing.getInstance();
        Set<String> t1 = new HashSet<>(Arrays.asList("tag1", "tag2"));
        Set<String> t2 = new HashSet<>(Arrays.asList("tag3", "私密"));
        Set<String> t3 = new HashSet<>(Arrays.asList("tag4", "tag5","tag6","tag7","tag8","tag9"));


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //Picture p1= new Picture(20,f1,"画师18", "作品19",timestamp, t3);
        Picture temp = dp.getPicture(3);

        List<PictureInfo> list = dp.listPictures();
      for(PictureInfo p : list){
          System.out.println(p.getNetworkSeq());
      }

        System.out.println("运行结束");



    }
}
