package mz.isctem.agrogest.repo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Persistencia do estado em ficheiro com serializacao Java (ObjectOutputStream / ObjectInputStream).
 * A escrita e atomica: grava primeiro num ficheiro temporario e so depois o move para o lugar do
 * ficheiro definitivo. Assim, uma falha a meio da gravacao nunca corrompe o ultimo estado bom.
 */
public final class Persistencia {

    private static final Logger LOG = Logger.getLogger(Persistencia.class.getName());
    private static final String NOME_FICHEIRO = "agrogest.ser";

    private Persistencia() {
    }

    /**
     * Local do ficheiro: propriedade de sistema "agrogest.data" se existir;
     * senao CATALINA_BASE/data/agrogest.ser; senao pasta do utilizador.
     */
    public static Path ficheiro() {
        String prop = System.getProperty("agrogest.data");
        if (prop != null && !prop.isBlank()) return Paths.get(prop);
        String base = System.getProperty("catalina.base");
        if (base != null) return Paths.get(base, "data", NOME_FICHEIRO);
        return Paths.get(System.getProperty("user.home"), "agrogest-data", NOME_FICHEIRO);
    }

    /** Grava o estado atual de forma atomica. */
    public static void gravar(DataStore ds) throws IOException {
        Path destino = ficheiro();
        Files.createDirectories(destino.getParent());
        Path tmp = destino.resolveSibling(destino.getFileName() + ".tmp");

        // Bloqueia escritas concorrentes enquanto o estado e serializado
        synchronized (ds) {
            try (FileOutputStream fos = new FileOutputStream(tmp.toFile());
                 ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fos))) {
                out.writeObject(ds);
                out.flush();
                fos.getFD().sync(); // garante que os dados chegaram ao disco
            }
        }
        try {
            Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, destino, StandardCopyOption.REPLACE_EXISTING);
        }
        LOG.info("AgroGest: estado gravado em " + destino);
    }

    /**
     * Restaura o estado do ficheiro. Devolve null se o ficheiro nao existir ou estiver corrompido
     * (neste caso o ficheiro e renomeado para .corrompido e o sistema arranca com estado limpo).
     */
    public static DataStore carregar() {
        Path f = ficheiro();
        if (!Files.exists(f)) return null;
        try (InputStream is = Files.newInputStream(f);
             ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(is))) {
            DataStore ds = (DataStore) in.readObject();
            LOG.info("AgroGest: estado restaurado de " + f);
            return ds;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            LOG.log(Level.SEVERE, "AgroGest: ficheiro de estado invalido, a arrancar com estado limpo", e);
            try {
                Files.move(f, f.resolveSibling(f.getFileName() + ".corrompido"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
                // nada a fazer
            }
            return null;
        }
    }

    /** Elimina o ficheiro gravado (os dados em memoria nao sao afetados). */
    public static boolean eliminar() throws IOException {
        return Files.deleteIfExists(ficheiro());
    }

    public static boolean existe() {
        return Files.exists(ficheiro());
    }
}
