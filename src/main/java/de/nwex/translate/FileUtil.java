package de.nwex.translate;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.nwex.translate.TextUtil.stackTraceToString;

public class FileUtil
{
    /**
     * Returns the full content of a text file, separated by {@link System#lineSeparator()}
     *
     * @param file The file to read from
     *
     * @return The full content of the given text file
     *
     * @see #getTextContent(String)
     */
    public static String getTextContent(File file)
    {
        StringBuilder content = new StringBuilder();

        fileInputIterator(file, (line, lineNumber) -> content.append(line).append(System.lineSeparator()));

        return content.toString();
    }

    /**
     * Returns the full content of a text file, separated by {@link System#lineSeparator()}
     *
     * @param filepath The path and name of the file to read from
     *
     * @return The full content of the given text file
     *
     * @see #getTextContent(File)
     */
    public static String getTextContent(String filepath)
    {
        return getTextContent(new File(filepath));
    }

    public static void fileInputIterator(File file, BiConsumer<String, Integer> iterator)
    {
        try
        {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int count = 0;

            while((line = bufferedReader.readLine()) != null)
            {
                iterator.accept(line, count);

                count++;
            }

            fileReader.close();
            bufferedReader.close();
        }
        catch(Exception exception)
        {
            Chat.error(new LiteralText("File"), new LiteralText(exception.getClass().getName() + ": " + exception.getMessage()).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(stackTraceToString(exception)).formatted(Formatting.RED)))));
        }
    }

    public static void fileInputIterator(String filepath, BiConsumer<String, Integer> iterator)
    {
        fileInputIterator(new File(filepath), iterator);
    }

    public static void fileOutputIterator(File file, List<Function<Integer, String>> iterators, Boolean overwrite)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(file);
            AtomicInteger count = new AtomicInteger(0);

            if(overwrite)
            {
                fileWriter.write("");
            }

            iterators.forEach((iterator) ->
            {
                try
                {
                    fileWriter.append(iterator.apply(count.getAndIncrement()));
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            });

            fileWriter.close();
        }
        catch(Exception exception)
        {
            Chat.error(new LiteralText("File"), new LiteralText(exception.getClass().getName() + ": " + exception.getMessage()).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(stackTraceToString(exception)).formatted(Formatting.RED)))));
        }
    }

    public static void fileOutputIterator(String filepath, List<Function<Integer, String>> iterators, Boolean overwrite)
    {
        fileOutputIterator(new File(filepath), iterators, overwrite);
    }

    /**
     * Overwrites and appends the given content to a file
     *
     * @param file    The file to write to
     * @param content The content to write to the file
     *
     * @see #setTextContent(String, String)
     */
    public static void setTextContent(File file, String content)
    {
        fileOutputIterator(file, new ArrayList<>(Collections.singleton(lineNumber -> content)), true);
    }

    /**
     * Overwrites and appends the given content to a file
     *
     * @param filepath The path and name of the file to write to
     * @param content  The content to write to the file
     *
     * @see #setTextContent(File, String)
     */
    public static void setTextContent(String filepath, String content)
    {
        setTextContent(new File(filepath), content);
    }

    /**
     * Appends the given content to a file
     *
     * @param file    The file to write to
     * @param content The content to write to the file
     *
     * @see #setTextContent(File, String)
     */
    public static void appendTextContent(File file, String content)
    {
        fileOutputIterator(file, new ArrayList<>(Collections.singleton(lineNumber -> content)), false);
    }

    /**
     * Appends the given content to a file
     *
     * @param filepath The path and name of the file to write to
     * @param content  The content to write to the file
     *
     * @see #setTextContent(File, String)
     */
    public static void appendTextContent(String filepath, String content)
    {
        appendTextContent(new File(filepath), content);
    }

    /**
     * Returns the running directory + {@link File#separator} + the specified filename + {@link File#separator}
     *
     * @param fileName The specified fileName
     *
     * @return The path as a string
     */
    public static String getAbsolutePath(String fileName)
    {
        return System.getProperty("user.dir") + File.separator + fileName + File.separator;
    }

    /**
     * Iterates over a directory and running the iterator on every file
     *
     * @param directory The file of the directory to iterate over
     * @param iterator  The iterator implementation (can be a lambda expression)
     */
    public static void directoryIterator(File directory, Consumer<File> iterator)
    {
        Arrays.asList(Objects.requireNonNull(directory.listFiles())).forEach(iterator);
    }

    /**
     * Iterates over a directory and running the iterator on every file
     *
     * @param directory The path to the directory to iterate over
     * @param iterator  The iterator implementation (can be a lambda expression)
     */
    public static void directoryIterator(String directory, Consumer<File> iterator)
    {
        directoryIterator(new File(directory), iterator);
    }

    /**
     * Iterates over a directory and running the iterator on every file. The implemented filter deferments if the file
     * gets iterated over
     *
     * @param directory The file of the directory to iterate over
     * @param filter    The filer implementation (can be a lambda expression)
     * @param iterator  The iterator implementation (can be a lambda expression)
     */
    public static void directoryIterator(File directory, Function<File, Boolean> filter, Consumer<File> iterator)
    {
        Arrays.stream(Objects.requireNonNull(directory.listFiles())).filter(filter::apply).forEach(iterator);
    }

    /**
     * Iterates over a directory and running the iterator on every file. The implemented filter deferments if the file
     * gets iterated over
     *
     * @param directory The path to the directory to iterate over
     * @param filter    The filer implementation (can be a lambda expression)
     * @param iterator  The iterator implementation (can be a lambda expression)
     */
    public static void directoryIterator(String directory, Function<File, Boolean> filter, Consumer<File> iterator)
    {
        directoryIterator(new File(directory), filter, iterator);
    }
}
