package jforgame.commons.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class scanner
 */
public final class ClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    /**
     * Default filter (no implementation)
     */
    private final static Predicate<Class<?>> EMPTY_FILTER = clazz -> true;

    /**
     * Scans all class files under the directory
     *
     * @param scanPackage the root package path to search
     * @return the list of all classes
     */
    public static Set<Class<?>> listClasses(String scanPackage) {
        return listClasses(scanPackage, EMPTY_FILTER);
    }

    /**
     * Returns all subclasses (excluding abstract classes)
     *
     * @param scanPackage the path to scan
     * @param parent      parent class type
     */
    public static Set<Class<?>> listAllSubclasses(String scanPackage, Class<?> parent) {
        return listClasses(scanPackage, clazz -> parent.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers()));
    }

    /**
     * Returns all classes with the specified annotation
     *
     * @param scanPackage the root package path to search
     * @param <A>         annotation type parameter, used to specify the annotation type to find
     * @param annotation  the target annotation type
     * @return the list of all classes with the specified annotation
     */
    public static <A extends Annotation> Set<Class<?>> listClassesWithAnnotation(String scanPackage, Class<A> annotation) {
        return listClasses(scanPackage, clazz -> clazz.getAnnotation(annotation) != null);
    }

    /**
     * Scans all class files under the directory
     *
     * @param pack   the package path
     * @param filter custom class filter
     * @return all the classes that meet filter rule
     */
    public static Set<Class<?>> listClasses(String pack, Predicate<Class<?>> filter) {
        Set<Class<?>> result = new LinkedHashSet<>();
        // Whether to iterate recursively
        boolean recursive = true;
        // Get the package name and replace it
        String packageDirName = pack.replace('.', '/');
        // Define an enumeration set and loop to process things in this directory
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // Continue iterating
            while (dirs.hasMoreElements()) {
                // Get the next element
                URL url = dirs.nextElement();
                // Get the protocol name
                String protocol = url.getProtocol();
                // If it is saved on the server as a file
                if ("file".equals(protocol)) {
                    // Get the physical path of the package
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // Scan all files under the package as files and add them to the collection
                    findAndAddClassesInPackageByFile(pack, filePath, recursive, result, filter);
                } else if ("jar".equals(protocol)) {
                    // If it is a jar package file
                    Set<Class<?>> jarClasses = findClassFromJar(url, pack, packageDirName, recursive, filter);
                    result.addAll(jarClasses);
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }

        return result;
    }

    private static Set<Class<?>> findClassFromJar(URL url, String packageName, String packageDirName, boolean recursive, Predicate<Class<?>> filter) {
        Set<Class<?>> result = new LinkedHashSet<>();
        try {
            // Get the jar
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            // Get an enumeration class from this jar package
            Enumeration<JarEntry> entries = jar.entries();
            // Similarly, iterate in a loop
            while (entries.hasMoreElements()) {
                // Get an entity in the jar, can be a directory and some other files in the jar package such as META-INF and other files
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // If it starts with /
                if (name.charAt(0) == '/') {
                    // Get the subsequent string
                    name = name.substring(1);
                }
                // If the first half is the same as the defined package name
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // If it ends with "/" it is a package
                    if (idx != -1) {
                        // Get the package name, replace "/" with "."
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    // If it can iterate further and is a package
                    if ((idx != -1) || recursive) {
                        // If it is a .class file and not a directory
                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            // Remove the trailing ".class" to get the real class name
                            String className = name.substring(packageName.length() + 1, name.length() - 6);
                            try {
                                // Add to classes
                                Class<?> c = Class.forName(packageName + '.' + className);
                                if (filter.test(c)) {
                                    result.add(c);
                                }
                            } catch (ClassNotFoundException e) {
                                logger.error("", e);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return result;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes, Predicate<Class<?>> filter) {
        File dir = new File(packagePath);
        // If it doesn't exist or is not a directory, return directly
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // If it exists, get all files under the package including directories
        // Custom filter rule: if it can loop (contains subdirectories) or is a file ending with .class (compiled java class file)
        File[] files = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // Loop through all files
        assert files != null;
        for (File file : files) {
            // If it is a directory, continue scanning
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes, filter);
            } else {
                // If it is a java class file, remove the trailing .class, leaving only the class name
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // Add to the collection
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className);
                    if (filter.test(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("", e);
                }
            }
        }
    }

}