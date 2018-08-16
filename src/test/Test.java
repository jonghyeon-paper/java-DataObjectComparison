package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import compare.DataObjectComparison;

public class Test {

    public static void main(String[] args) throws ParseException {
        DataObject1 object1 = new DataObject1();
        object1.setName("보노보노");
        object1.setGender("MAN");
        object1.setCountry("KOREA");
        object1.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));
        object1.setMarryDate(null);
        object1.setPhoneNo(null);

        DataObject2 object2 = new DataObject2();
        object2.setName("보노보노");
        object2.setEngName("Bonobono");
        object2.setGender("MAN");
        object2.setCountry("KOREA");
        object2.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));
        object2.setMarryDate(null);
        object2.setPhoneNo(null);
        object2.setCellularNo(null);
        object2.setZip("12345");
        object2.setAddress("대한민국 어딘가에");

        System.out.println(DataObjectComparison.compare(object1, object2, false));

        List<DataObject1> object1List = new ArrayList<>();
        object1List.add(object1);

        List<DataObject2> object2List = new ArrayList<>();
        object2List.add(object2);

        System.out.println(DataObjectComparison.compare(object1List, object2List, false));
    }

}
