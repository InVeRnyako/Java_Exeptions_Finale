import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;
import java.util.StringJoiner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Введите данные в формате:\n" + 
            "Фамилия Имя Отчество Дата_Рождения(дд.мм.гггг) Номер_Телефона Пол(m/f)");

        // String test = "Иваныов Иван Ивановичч 01.01.2000 79888888888 m";
        // System.out.println(test);

        String userInput = System.console().readLine();
        splitInputString(userInput);
    }

    public static void splitInputString(String inputString){
        try {
            validInputToSplit(inputString);
            splitCheckAndSave(inputString);
        } catch (Exception e) {
            // System.out.println(e);
            if (e.getMessage().contains("файл")){
                System.out.println("Ошибка работы с файлом:\n" + e);
            } else {
                System.out.println("Ошибка ввода данных.\n" + e);
            }
        }
        System.out.println("Данные успешно сохранены.");
    }

    public static void validInputToSplit(String inputString) throws Exception{
        int expectedAmountOfSpaces = 5;

        // Счет символов пробела.
        int spaceCount = 0;
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == ' ')
                spaceCount++;
        }

        if (spaceCount < expectedAmountOfSpaces) {
            throw new Exception ("Недостаточно данных.");    
        } else if (spaceCount > expectedAmountOfSpaces){
            throw new Exception("Избыточные данные.");
        }
    }

    public static void splitCheckAndSave(String inputString) throws Exception{
        String[] splitedInput = inputString.split(" ");
        String sex = "";
        String phoneNumber = ""; // В int не влезает, остается String'ом
        LocalDate birthDay = LocalDate.now();
        String firstName = null;
        String middleName = null;
        String lastName = null;

        for (String inputPart : splitedInput) {
            if (inputPart.length() == 1){ // Один символ - пол.
                if (inputPart.equals("f") || inputPart.equals("m")){
                    sex = inputPart;
                } else {
                    throw new Exception("Неверно указан пол.");
                }
                continue;
            }

            if (inputPart.matches("[0-9]+")){ // Только цифры - номер телефона
                if (inputPart.length() == 11){ // 11 цифр - международный формат номера телефона
                    phoneNumber = inputPart;
                } else {
                    throw new Exception("Неверно указан номер телефона.");
                }
                continue;
            }

            if (inputPart.matches("[0-9.]+")){ // Цифры и точки - дата рождения
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern ( "dd.MM.uuuu" )
                                                        .withResolverStyle(ResolverStyle.STRICT);
                try {
                    birthDay = LocalDate.parse ( inputPart , dateTimeFormatter );
                } catch ( DateTimeParseException e ) {
                    throw new Exception("Неверный формат даты рождения.");
                }
                if (birthDay.isAfter(LocalDate.now()) || birthDay.isBefore(birthDay.minusYears(100)))
                    throw new Exception("Введена некоректная дата рождения.");
                continue;
            }

            if (inputPart.matches("[а-яёА-ЯЁ-]+")){ // Только русские буквы - ФИО, '-' для сдвоенных фамилий
                if (inputPart.length() > 25) // Из всех вариантов - рекордная фамилия 24 символа, по результатам быстрого гугления.
                    throw new Exception("Слишком длинные Фамилия/Имя/Отечество.");
                
                // Ожидаем заполнения ФИО в именно таком формате.
                if (lastName == null){
                    lastName = inputPart;
                } else if (firstName == null){
                    firstName = inputPart;
                } else if (middleName == null){
                    middleName = inputPart;
                } else {
                    throw new Exception("Избыток данных формата ФИО.");
                }
                continue;
            }            
            throw new Exception("Некорректный формат данных");
        }
        saveData(lastName, firstName, middleName, birthDay, phoneNumber, sex);
    }

    public static boolean saveData(String lastName, String firstName, String middleName,
                                    LocalDate birthDate, String phoneNumber, String sex) throws Exception{
        StringJoiner stringToSave = new StringJoiner("><","<",">\n");
        stringToSave.add(lastName).add(firstName).add(middleName)
                    .add(birthDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")))
                    .add(phoneNumber).add(sex);
        
        // Определение папки для сохранения данных, создание в случае отстутствия
        String pathToData = "SavedData";
        Files.createDirectories(Paths.get(pathToData));
        String fileName = pathToData + "/" + lastName + ".txt";
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "utf-8"))){
            writer.append(stringToSave.toString());
        } catch (Exception exc) { // Сообщения об ошибках по работе с файлами должны содержать "файл" в тексте
            StringJoiner stackString = new StringJoiner("\n");
            for (StackTraceElement element : exc.getStackTrace()) {
                    stackString.add(element.toString());
                }
            if (exc instanceof UnsupportedEncodingException)
                throw new Exception("Ошибка кодировки при сохранении файла.\n" + stackString);
            if (exc instanceof SecurityException)
                throw new Exception("Ошибка доступа к записываемому файлу.\n" + stackString);
            if (exc instanceof FileNotFoundException)
                throw new Exception("Ошибка при создании файла записи\n" + stackString);
        }
        return true;
    }
}