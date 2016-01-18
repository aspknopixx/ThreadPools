import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Task {
   static ArrayList<File> list = new ArrayList<>();
    static ArrayList<String> listWrite = new ArrayList<>();
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            // Считываем исходный каталог для поиска файлов.
            System.out.print("Введите исходную директорию для поиска файлов:");
            final String directoryPath = reader.readLine();
            System.out.print("Введите ключевое слово:");
            final String keyWord = reader.readLine();
            processDirectory(directoryPath);
            MathCount(list,keyWord);
            //запись в файл
            System.out.println("Для входа из программы введите < = > exit :");
            final String exitWord = reader.readLine();
            String word = "exit";
            if (word.contains(exitWord)){
                WriteToFile();
            }else{
                System.out.println("Выход из программы.");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private static void processDirectory(String directory) {

        // Получаем список доступных файлов в указанной директории и поддиректориях.
        File files = new File(directory);
        for (File s : files.listFiles()) {
            if (s.isFile()) {
                list.add(s);
            } else if (s.isDirectory()) {
                processDirectory(s.getAbsolutePath());
            }
        }
    }
    // Просматриваем строки и подсчитываем по маске
    private static void MathCount(ArrayList<File> s,String keyword){
        //Создаем 10 параллельных потоков.
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (File f : s) {
            if (!f.isFile()) {
                continue;
            }
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                        int lines = 0;
                        String serach;
                        while ((serach = reader.readLine()) != null) {
                            if (serach.contains(keyword) || serach.isEmpty()){
                                ++lines;
                            }
                            listWrite.add(Thread.currentThread().getName() + " => "+ f.getName() + " => "+ String.valueOf(lines));
                        }
                        System.out.println("Поток: " + Thread.currentThread().getName() + ". Файл: " + f.getName() + ". Количество строк: " + lines);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // Новые задачи более не принимаем, выполняем только оставшиеся.
        service.shutdown();
        // Ждем завершения выполнения потоков не более 10 минут.
        try {
            service.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void WriteToFile() throws FileNotFoundException {
        try(FileWriter writer = new FileWriter("d:\\output.txt", false))
        {
            for (int i = 0; i < listWrite.size(); i++) {
                writer.write(String.valueOf(listWrite));
            }

        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}



