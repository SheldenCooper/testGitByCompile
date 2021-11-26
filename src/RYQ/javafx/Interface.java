package RYQ.javafx;
import RYQ.grammar.*;
import RYQ.grammar.Node;
import RYQ.test.OPG;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A sample that demonstrates styling a hidden split pane with CSS.
 */
public class Interface extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        TextArea textCode = new TextArea();textCode.setPrefColumnCount(40);textCode.setPrefRowCount(60);textCode.setWrapText(true);
        TextArea textInfo = new TextArea();textInfo.setPrefColumnCount(40);textInfo.setPrefRowCount(10);textInfo.setWrapText(true);
        TextArea textResoult = new TextArea();textResoult.setPrefColumnCount(40);textResoult.setPrefRowCount(50);textResoult.setWrapText(true);
        //为了实现代码显示行数，我们进HBOx内创建行标签文本框和代码文本框
        TextArea textCount = new TextArea();textCount.setPrefColumnCount(6);textCount.setPrefRowCount(30);

        HBox hbox_container = new HBox();
        hbox_container.getChildren().addAll(textCount,textCode);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(textInfo,textResoult);
//        hBox.getChildren().addAll(textCode,vbox);
        SplitPane splitPaneV = new SplitPane();
        splitPaneV.setOrientation(Orientation.VERTICAL);
        splitPaneV.getItems().addAll(textResoult,textInfo);
        SplitPane splitPaneH = new SplitPane();
        splitPaneH.getItems().addAll(hbox_container,splitPaneV);

        //part of menu
        MenuBar menuBar = new MenuBar();

        // --- Menu File
        Menu menuFile = new Menu("File");
        MenuItem add = new MenuItem("open");
        add.setOnAction((ActionEvent t) -> {
            FileChooser fileChooser1 = new FileChooser();
            fileChooser1.setTitle("Open file");
            File file = fileChooser1.showSaveDialog(primaryStage);
            textCode.clear();
            System.out.println(file.toString());
            textCode.appendText(ReadFile.readFile(file.toString()));
        });

        MenuItem save = new MenuItem("save all");
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        save.setOnAction((ActionEvent t) -> {
            StringSelection ss = new StringSelection(textCode.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            clipboard.setContents(ss, null);
        });

        MenuItem clear = new MenuItem("close");
        clear.setOnAction((ActionEvent t) -> {
            textInfo.clear();
            textCode.clear();
            textResoult.clear();
        });

        MenuItem paste1 = new MenuItem("paste1");
        paste1.setOnAction((ActionEvent t) -> {
            textInfo.clear();
            textInfo.appendText(textCode.getText());
        });

        MenuItem paste2 = new MenuItem("paste2");
        paste2.setOnAction((ActionEvent t) -> {
            textResoult.clear();
            textResoult.appendText(textCode.getText());
        });
        menuFile.getItems().addAll(add, save, new SeparatorMenuItem(), clear,paste1,paste2);
        // --- Menu Arithmetic
        Menu menuArithmetic = new Menu("Arithmetic");
        MenuItem NDM = new MenuItem("NDM");
        NDM.setOnAction((ActionEvent t) -> {

            //
            NDMfa ndMfa = new NDMfa();
            ndMfa.doNDMFA();
            //
            textInfo.clear();
            textResoult.clear();
            textResoult.appendText("NFA:"+"\n");
            for(String str:ndMfa.list_NFA){
                textResoult.appendText(str+"\n");
            }
            textInfo.appendText("DFA:"+"\n");
            for(String str:ndMfa.list_DFA){
                textInfo.appendText(str+"\n");
            }
        });

        MenuItem LL = new MenuItem("LL");
        LL.setAccelerator(KeyCombination.keyCombination("LL"));
        LL.setOnAction((ActionEvent t) -> {
            Lexical lexical = new Lexical(textCode.getText()+"\n");
            lexical.LexicalAnal();
            ArrayList<String> list = lexical.getResultList();
            ArrayList<String> list_result = new ArrayList<>();
            for(String str:list){
                String str_temp = str.substring(0,str.indexOf("["));
                String[] strs = str_temp.split("\\s+");
                list_result.add("["+strs[1]+","+strs[0]+"]");
            }
            String path_file = Tool.ChangeAbsolutePathRoot("Lex.tys");
            try {
                ReadFile.cleanFile(path_file);
                ReadFile.bufferWrite(path_file,list_result);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //
            LL1 ll1 = new LL1();
            //
            path_file = Tool.ChangeAbsolutePathRoot("LL1Table.txt");
            ArrayList<String> list_readRsult = ReadFile.readFileList(path_file);
            for(String str:list_readRsult){
                textInfo.appendText(str+"\n");
            }
            path_file = Tool.ChangeAbsolutePathRoot("LL1Process.txt");
            list_readRsult = ReadFile.readFileList(path_file);
            for(String str:list_readRsult){
                textResoult.appendText(str+"\n");
            }
        });

        MenuItem Arith3 = new MenuItem("算符优先");
        Arith3.setOnAction((ActionEvent t) -> {
            Lexical lexical = new Lexical(textCode.getText()+"\n");
            lexical.LexicalAnal();
            ArrayList<String> list = lexical.getResultList();
            ArrayList<String> list_result = new ArrayList<>();
            for(String str:list){
                String str_temp = str.substring(0,str.indexOf("["));
                String[] strs = str_temp.split("\\s+");
                list_result.add("["+strs[1]+","+strs[0]+"]");
            }
            String path_file = Tool.ChangeAbsolutePathRoot("Lex.tys");
            try {
                ReadFile.cleanFile(path_file);
                ReadFile.bufferWrite(path_file,list_result);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //
            OPG opgMain = new OPG();
            opgMain.start();
            //
            textInfo.clear();
            textResoult.clear();
            path_file = Tool.ChangeAbsolutePathRoot("OPGTable.txt");
            ArrayList<String> list_readRsult = ReadFile.readFileList(path_file);
            for(String str:list_readRsult){
                textInfo.appendText(str+"\n");
            }
            path_file = Tool.ChangeAbsolutePathRoot("OPGResult.txt");
            list_readRsult = ReadFile.readFileList(path_file);
            for(String str:list_readRsult){
                textResoult.appendText(str+"\n");
            }

        });

        MenuItem LR = new MenuItem("LR");
        LR.setOnAction((ActionEvent t) -> {
            Lexical lexical = new Lexical(textCode.getText()+"\n");
            lexical.LexicalAnal();
            ArrayList<String> list = lexical.getResultList();
            ArrayList<String> list_result = new ArrayList<>();
            for(String str:list){
                String str_temp = str.substring(0,str.indexOf("["));
                String[] strs = str_temp.split("\\s+");
                list_result.add("["+strs[1]+","+strs[0]+"]");
            }
            String path_file = Tool.ChangeAbsolutePathRoot("Lex.tys");
            try {
                ReadFile.cleanFile(path_file);
                ReadFile.bufferWrite(path_file,list_result);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //
            OPG opgMain = new OPG();
            opgMain.start();
            //
            path_file = Tool.ChangeAbsolutePathRoot("OPGResult.txt");
            ArrayList<String> list_readRsult = ReadFile.readFileList(path_file);
            for(String str:list_readRsult){
                textResoult.appendText(str+"\n");
            }
        });

        menuArithmetic.getItems().addAll(NDM, LL, new SeparatorMenuItem(), Arith3,LR);
        // --- Menu Edit
        Menu menuEdit = new Menu("Edit");
        // --- Menu Lexical
        Menu menuLexical = new Menu();
        Label label_Lexical = new Label("Lexical");
        menuLexical.setGraphic(label_Lexical);
        label_Lexical.setOnMouseClicked((event) -> {
            Lexical lexical = new Lexical(textCode.getText());
            lexical.LexicalAnal();
            textInfo.clear();
            textResoult.clear();

            for(String str:lexical.getResultList()){
                textResoult.appendText(str+"\n");
            }
            for(String str:lexical.getErrorList()){
                textInfo.appendText(str+"\n");
            }
        });
        //--- Menu Meaning
        Menu menuMeaning = new Menu();
        Label label_Meaning = new Label("Meaning");
        menuMeaning.setGraphic(label_Meaning);
        label_Meaning.setOnMouseClicked((event) -> {
            Lexical lexical = new Lexical(textCode.getText());
            lexical.LexicalAnal();
            ArrayList<String> list = lexical.getResultList();
            BlockBody blockBody = new BlockBody(GrammerTool.changeToNode(list),true,0);
            blockBody.testTotal();

            textInfo.clear();
            textResoult.clear();

            for(String str:blockBody.getList_entryInfo()){
                textResoult.appendText(str+"\n");
            }
            textInfo.appendText("文法错误：\n");
            for(String str:blockBody.getList_error()){
                textInfo.appendText(str+"\n");
            }
            textInfo.appendText("语义错误：\n");
            for(String str:blockBody.getList_mean_error()){
                textInfo.appendText(str+"\n");
            }
        });
        //--- Menu menuFour
        Menu menuFour = new Menu();
        Label label_Four = new Label("Four");
        menuFour.setGraphic(label_Four);
        label_Four.setOnMouseClicked((event) -> {
            Lexical lexical = new Lexical(textCode.getText());
            lexical.LexicalAnal();
            ArrayList<String> list = lexical.getResultList();
            BlockBody blockBody = new BlockBody(GrammerTool.changeToNode(list),true,0);
            blockBody.testTotal();

            textInfo.clear();
            textResoult.clear();
            for(String str:blockBody.getList_entryInfo()){
                textResoult.appendText(str+"\n");
            }
            textInfo.appendText("四元式结果如下"+"\n");
            for(int i=0;i<blockBody.getList_nodeFour().size();i++){
                textInfo.appendText(i+"\t"+blockBody.getList_nodeFour().get(i)+"\n");
            }
        });
        //--- Menu menuFour
        Menu menuTarget = new Menu();
        Label label_Target = new Label("Target");
        menuTarget.setGraphic(label_Target);
        label_Target.setOnMouseClicked((event) -> {
            Lexical lexical = new Lexical(textCode.getText());
            lexical.LexicalAnal();
            ArrayList<String> list = lexical.getResultList();
            BlockBody blockBody = new BlockBody(GrammerTool.changeToNode(list),true,0);
            //
            blockBody.creatFour();
            TargetCode targetCode =  new TargetCode(blockBody);
            targetCode.changeToCompileCode();
            textInfo.clear();
            textResoult.clear();
            for(String str:blockBody.getList_entryInfo()){
                textResoult.appendText(str+"\n");
            }
            textResoult.appendText("四元式结果如下"+"\n");
            for(int i=0;i<blockBody.getList_nodeFour().size();i++){
                textResoult.appendText(i+"\t"+blockBody.getList_nodeFour().get(i)+"\n");
            }
            textInfo.clear();
            for(String str:targetCode.getList_targetCode()){
                textInfo.appendText(str+"\n");
            }

        });
        //--- Menu help
        Menu menuHelp = new Menu();
        Label label = new Label("help");
        menuHelp.setGraphic(label);
        label.setOnMouseClicked((event) -> {
            File directory = new File("");
            String path_file = directory.getAbsolutePath()+"\\src\\javafx\\help.CHM";
            System.out.println("原："+path_file);
            path_file= path_file.replaceAll("\\\\", "\\\\\\\\");
            File file = new File(path_file);
            System.out.println(path_file);

            //当前目录下的CHM类型帮助文件
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);              //打开文件
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
            });
        //为代码文本框设置监听
        textCode.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int num_code = Tool.CountNumWrap(textCode.getText());
                int num_count = Tool.CountNumWrap(textCount.getText());
                if(num_code>num_count){
                    for (int i = num_count+1; i <=num_code ; i++) {
                        textCount.appendText(+i+"\n");
                    }
                }

            }
        });
        splitPaneH.setStyle("-fx-control-inner-background: rgb(34, 31, 31); -fx-text-fill: rgb(158, 152, 152);");
        menuBar.getMenus().addAll(menuFile, menuEdit, menuLexical,menuMeaning,menuFour,menuTarget,menuArithmetic,menuHelp);
        Scene scene = new Scene(new VBox(), 800, 600);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, splitPaneH);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setHeight(1000);
        primaryStage.setTitle("RYQcomplier");
        primaryStage.setWidth(1000);
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}