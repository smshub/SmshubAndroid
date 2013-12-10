package com.donhuan.SmshubAndroid;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Dh
 * Date: 23.11.13
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public class CommonFunctions {

    ContentResolver contentResolver;
    Uri SMSBASE_URI;
    String LOG_TAG = "CommonFunctions";


    CommonFunctions (ContentResolver contentResolver, Uri SMSBASE_URI)
    {
        this.contentResolver = contentResolver;
        this.SMSBASE_URI = SMSBASE_URI;
    }


    public void putInfoToDB (String BANKNAME, String BANKNUM, String STORENAME, String DATE, String TIME, String SPENDMON, String RESTMON)
    {
        SMSDataBaseProvider sqh = new SMSDataBaseProvider();
        //---- Работа с базой данных
        ContentValues cv = new ContentValues();
        cv.put(SMSDataBaseProvider.BANKNAME, BANKNAME);
        cv.put(SMSDataBaseProvider.BANKNUM, BANKNUM);
        cv.put(SMSDataBaseProvider.STORENAME, STORENAME);
        cv.put(SMSDataBaseProvider.DATE, DATE);
        cv.put(SMSDataBaseProvider.TIME, TIME);
        cv.put(SMSDataBaseProvider.SPENDMON, SPENDMON);
        cv.put(SMSDataBaseProvider.RESTMON, RESTMON);
        cv.put(SMSDataBaseProvider.ISINFIN, "0"); //было ли сообщение добавлено в финансисто

        Uri newUri = contentResolver.insert(SMSBASE_URI, cv);
        Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
    }


    /*
    * Функция сканирования сообщения.
    *
    * Поиск нужного формата с использованием регулярных выражений,
    * но с условием того, что порядок входных данных строго фиксирован.
    * Поэтому используются флаги.
    *
    * message   - текст сообщения
    * */
    public String[] scanMessage(String message) {
        String[] items = {"", "", "", "", "", "", ""};

        String[] splitString = (message.split("\\s+"));
        boolean[] list = new boolean[7];                                                                                //Банк, номер, дата, время, оплата, магазин, остаток
        int countAmounts = 0;
        for (int i = 0; i < splitString.length; i++) {
            String word = splitString[i].toLowerCase();

            String regexpMonth =
                    "(янв\\w*" +
                            "|фев\\w*" +
                            "|м\\w?рт\\w?" +
                            "|апр\\w*" +
                            "|ма\\w?" +
                            "|июн\\w?" +
                            "|июл\\w?" +
                            "|авг\\w*" +
                            "|сен\\w*" +
                            "|н\\w{2}?бр\\w?" +
                            "|дек\\w*)";

            if (word.matches("[a-z]+|[а-я]+") && !list[0]) {                                                                //роверка на банк
                items[0] = splitString[i];
                list[0] = true;
            } else if (word.matches("\\d+.?") && !list[1]) {                                                                //Проверка на номер
                splitString[i] = dropItem(splitString[i]);
                items[1] = splitString[i];
                list[1] = true;
            } else if (word.matches("\\d{1,2}(" + regexpMonth + "|(\\.)\\d{2}((\\.)\\d{2,4})?)") && !list[2]) {             //Проверка на дату формата 1       (слитного)
                items[2] = splitString[i];
                list[2] = true;
            } else if (word.matches(regexpMonth) && splitString[i - 1].matches("\\d{1,2}") && !list[2]) {                   //Проверка на дату формата 2       (раздельного)
                String yy = "";
                if (splitString[i + 1].matches("\\d{2,4}?")) {
                    yy = splitString[i + 1];
                }
                items[2] = splitString[i - 1] + " " + splitString[i] + " " + yy;
                list[2] = true;
            } else if (word.matches("\\d{1,2}(:|,|(\\.))\\d{2}") && !list[3]) {                                              //Проверка на время
                items[3] = word;
                list[3] = true;
            } else if (word.matches("\\d+([._,]\\d*)?\\w*((\\.)|,)?") && list[0] && list[1] && list[2] && list[3]) {         //Проверка на суммы  (олько если до этого нашли номер, банк , дату и время)

                if (countAmounts == 0) {    //если мы нашли сумму второй раз - это сумма остатка, иначе все плохо
                    word = dropItem(word);
                    items[4] = word;
                    list[4] = true;
                } else if (countAmounts == 1) {
                    word = dropItem(word);
                    items[6] = word;
                    list[6] = true;
                } else {
                    list[4] = false;
                    list[6] = false;
                }
                countAmounts++;

            } else if (splitString[i].matches("[A-Z]+") && list[0] && list[1] && list[2] && list[3] && list[4]) {                       //Проверка на магазин
                items[5] += word + " ";
                list[5] = true;
            }
        }
        items = updateDetectedItems(items, list);
        return items;
    }

    /*
    * Если все распознанно, то пишем обновляем detectedItems, иначе делаем пустым
    * */
    private String[] updateDetectedItems(String[] items, boolean[] list) {
        String[] returnItems = new String[0];
        for (boolean aList : list) {
            if (!aList) returnItems = new String[1];
            else returnItems = items;
        }
        return returnItems;
    }

    /*
    * Функция обрезает возможные символы с конца строки
    * */
    String dropItem(String item) {
        for (int i = item.length(); i > 0; i--) {
            try {
                item = item.substring(0, i);
                Double.parseDouble(item);
                return item;
            } catch (Exception ignore) {
            }
        }
        return item;
    }
}
