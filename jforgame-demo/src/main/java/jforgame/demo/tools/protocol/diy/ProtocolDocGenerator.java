package jforgame.demo.tools.protocol.diy;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import jforgame.commons.util.ClassScanner;
import jforgame.commons.util.FileUtil;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


public class ProtocolDocGenerator {


    protected List<ProtocolFileDoc> docs = new LinkedList<>();

    public void collect() throws Exception {
        String rootPath = new File("src/main/java/").getAbsolutePath();
        Set<Class<?>> classSet = ClassScanner.listClassesWithAnnotation("jforgame.demo.game", MessageMeta.class);
        for (Class<?> e : classSet) {
            ProtocolFileDoc protocolDoc = new ProtocolFileDoc();
            printClassDoc(e, protocolDoc, rootPath);
        }
    }

    private void printClassDoc(Class<?> e, ProtocolFileDoc protocolFileDoc, String rootPath) throws Exception {
        // org.tea.game.gm.message.ReqGmCommand 转为 org\\game\\server\\game\\gm\\message\\ReqGmCommand
        String fileName = e.getName().replaceAll("\\.", "\\\\") + ".java";
        String code;
        try {
            code = FileUtil.readFullText(rootPath + File.separator + fileName);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow(() -> new NoSuchElementException("No value present"));
        // 提取类注释
        String classComment = "";
        for (ClassOrInterfaceDeclaration classDeclaration : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            if (classDeclaration.getJavadoc().isPresent()) {
                classComment = classDeclaration.getJavadoc().get().toText();
            }
        }
        protocolFileDoc.setClazz(e);
        if (GameMessageFactory.getInstance().contains(e)) {
            protocolFileDoc.setCmd(GameMessageFactory.getInstance().getMessageId(e) + "");
        }
        protocolFileDoc.setName(e.getSimpleName());
        protocolFileDoc.setDesc(classComment.trim());

        List<ProtocolFieldDoc> protocolFieldDocs = new LinkedList<>();
        protocolFileDoc.setFields(protocolFieldDocs);

        // 提取字段相关信息（包括注释、类型、名称）
        for (FieldDeclaration fieldDeclaration : cu.findAll(FieldDeclaration.class)) {
            String fieldComment = "";
            if (fieldDeclaration.getJavadoc().isPresent()) {
                fieldComment = fieldDeclaration.getJavadoc().get().toText();
            }
            String fieldName = fieldDeclaration.getVariable(0).getNameAsString();
            Field field = null;
            try {
                field = e.getDeclaredField(fieldName);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                // 静态字段/常量不导入
                continue;
            }
            Class<?> fieldType = field.getType();
            Class<?> eleType = fieldType;
            if (Collection.class.isAssignableFrom(fieldType)) {
                eleType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            } else if (fieldType.isArray()) {
                eleType = fieldType.getComponentType();
            }

            if (!eleType.isPrimitive() && !Number.class.isAssignableFrom(eleType) && eleType != String.class) {
                // 嵌套bean无需导入同级类
                if (Message.class.isAssignableFrom(protocolFileDoc.getClazz())) {
                    protocolFileDoc.getImportItems().add(eleType.getSimpleName());
                } else {
                    protocolFileDoc.getImportItems2().add(eleType.getSimpleName());
                }
            }

            ProtocolFieldDoc protocolFieldDoc = new ProtocolFieldDoc();
            protocolFieldDoc.setName(fieldName);
            protocolFieldDoc.setMeta(field);
            protocolFieldDoc.setDesc(fieldComment.replaceAll("\r\n", ""));
            protocolFieldDocs.add(protocolFieldDoc);
        }

        docs.add(protocolFileDoc);
    }

}