package parser;

import dto.Book;
import exception.CommandException;
import http.HttpConnection;

import java.net.MalformedURLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class CommandParser {


    private static CommandParser instance;
    public static ArrayList<ArrayList<String>> commands = new ArrayList<>();
    private static HttpConnection httpConnection;


    private CommandParser(Properties properties) throws MalformedURLException {
        String[] string_commands = properties.get("allowed-commands").toString().split("&");
        httpConnection = HttpConnection.getInstance((String) properties.get("server-add-url"), (String) properties.get("server-search-url"));
        for (String command: string_commands){
            commands.add(new ArrayList(Arrays.asList(command.split(" "))));
        }
        commands.forEach(System.out::println);
    }


    public void parseCommand(String command) throws CommandException {
        String[] parts_of_command = command.split(" ");
        String general_command = parts_of_command[0];
        if (general_command.equals("add")){
            if (parts_of_command.length == 6){
                Book book = new Book(parts_of_command[1], parts_of_command[2], parts_of_command[3], parts_of_command[4], parts_of_command[5]);
                httpConnection.sendAddCommand(book);
            }else{
                throw new CommandException("Команда add требует аргумент: имя книги");
            }
        }else if(general_command.equals("search")){
            if(parts_of_command.length == 3){
                String flag = parts_of_command[1];
                try {
                    switch (flag) {
                        case "-n":
                            httpConnection.sendSearchCommand(parts_of_command[2], "name");
                            break;
                        case "-k":
                            httpConnection.sendSearchCommand(parts_of_command[2], "keyword");
                            break;
                        case "-a":
                            httpConnection.sendSearchCommand(parts_of_command[2], "author");
                            break;
                        default:
                            throw new CommandException("Неверные флаги команды search");
                    }
                }catch (MalformedURLException e){
                    throw new CommandException("Ошибка подключения к серверу");
                }
            }else {
                throw new CommandException("Неверный синтаксис команды search");
            }
        }else if(general_command.equals("exit")){
            System.out.println("Досвидос");
            System.exit(0);
        }else{
            throw new CommandException(general_command + " не является командой");
        }


    }


    public static CommandParser getInstance(Properties properties) throws MalformedURLException {
        if (instance == null){
            instance = new CommandParser(properties);
        }
        return instance;
    }
}
