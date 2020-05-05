package com.dpaula.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * @author Fernando de Lima
 */
public class IO {

    //copia um arquivo para outro local
    public static void copyTo(Path source, File target) throws IOException {

        //gera a estrutura de diretorios caso nao exista
        target.getParentFile().mkdirs();

        //copia arquivo do source para o caminho com nome, da replace caso exista
        Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // escrevendo conteudo no arquivo
    public static void append(File target, String conteudo) throws IOException {

        // escrevendo no final do arquivo
        Files.write(target.toPath(), conteudo.getBytes(), StandardOpenOption.APPEND);
    }
}
