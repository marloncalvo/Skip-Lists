import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
import java.io.File;

public class Linter
{

    public static final String [] conditionals = {"if", "for", "while", "switch", "else if"};

    public static void main(String [] args) throws IOException
    {
        Scanner scan = new Scanner(new File(args[0]));

        int lineCounter = 0;
        int commentTab = 0;
        String commentLine = "";

        while (scan.hasNextLine())
        {
            String l = scan.nextLine();
            lineCounter++;
            
            String regexBracket = ".*\\Q(.*\\Q).*{";
            if (l.matches(regexBracket))
            {
                System.out.println("Bracket issue!");
                System.out.println(lineCounter + l);
                System.exit(1);
            }

            String regexComment = "\\s*" + "//.*";
            if (l.matches(regexComment))
            {
                commentTab = l.indexOf("/");
                commentLine = l;
            }
            else
            {
                if (commentTab != 0)
                {
                    String regexFirstChar = "\\S";
                    Pattern p = Pattern.compile(regexFirstChar);
                    Matcher m = p.matcher(l);
                    if (m.find())
                    {
                        if (commentTab != m.start())
                        {
                            System.out.println((lineCounter - 1) + commentLine);
                            System.out.println(lineCounter + l);
                            System.exit(1);
                        }

                        commentTab = 0;
                    }
                }
            }

            for (String cond : conditionals)
            {
                String regex = "\\s*" + cond + "\\s{0}" + Pattern.quote("(") + ".*";
                if (l.matches(regex))
                {
                    String blank = l.substring(l.indexOf(cond), l.indexOf(cond) + cond.length());
                    if (blank != " ")
                    {
                        System.out.println("Found spacing issue!");
                        System.out.println(lineCounter + " " + l);
                        System.exit(1);
                    }
                }
            }
        }
    }
}                   
