package com.author.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;

class ACConverterTest {
    ACConverter converter = new ACConverter();

    private static void throwsException(Executable executable, String expectedMessage) throws Exception {
       try{
           executable.execute();
           throw new Exception("Exception was expected but not thrown!");
       } catch (Throwable throwable) {
           if(expectedMessage!=null){
               if(throwable.getMessage()==null)
                   throw new Exception("Exception was thrown but message is null even though expected: "+expectedMessage);
               if(!throwable.getMessage().contains(expectedMessage))
                   throw new Exception("Exception was thrown but message ("+throwable.getMessage()+") does not contain expected: "+expectedMessage);
           }
       }
    }

    @Test
    void comments() throws IOException {
        Assertions.assertEquals("", converter.parseString("//comment"));
        Assertions.assertEquals("", converter.parseString("// comment"));
        Assertions.assertEquals("", converter.parseString("//              comment"));
        Assertions.assertEquals("", converter.parseString("                     //comment"));
        Assertions.assertEquals("", converter.parseString("                     //              comment"));
    }

    @Test
    void filePath() throws Exception {
        converter.parseString("/file/path");
        throwsException(() -> {
            converter.parseString("file/path");
        }, "File path must start with '/'");
    }

    @Test
    void variables() throws IOException {
        String actual = converter.parseString("int a = 10;");
        System.out.println(actual);
    }
}