/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slideshow;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author Marek
 */
public class FXMLDocumentController implements Initializable
{
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider sliderSpeed;
    @FXML
    private Label labelFileName;
    
    @FXML
    private VBox vboxLeft;
    
    @FXML
    private ToggleGroup tgLeft;
    @FXML
    private Button btnPlus;
    
    private List<SlideImages> slideShows;
    private ScheduledExecutorService executor;
    private int slideShowIdx = 1;
    private SlideImages slideShow;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        slideShows = new ArrayList<SlideImages>();
        setSlideShow(new SlideImages());
        slideShows.add(slideShow);
    }    
    
    @FXML
    private void onStart(ActionEvent event)
    {
        slideShow.start();
    }

    @FXML
    private void onStop(ActionEvent event)
    {
        slideShow.stop();
    }

    @FXML
    private void onLoad(ActionEvent event)
    {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Select images");
        fileDialog.getExtensionFilters().add(new ExtensionFilter("Images", 
            "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));        
        List<File> files = fileDialog.showOpenMultipleDialog(new Stage());
        if(files == null)
            return;
        List<Pair<String,Image>> imageList = new ArrayList<Pair<String,Image>>();
                
        if (!files.isEmpty())
        {
            for(File file: files)
            {
                String path = file.toURI().toString(); 
                imageList.add(new Pair(path,new Image(path)));
            }   
            slideShow.setImageList(imageList);
        }
    }

    @FXML
    private void onPrev(ActionEvent event) 
    {
        slideShow.stop();
        slideShow.prev();
    }

    @FXML
    private void onNext(ActionEvent event)
    {
        slideShow.stop();
        slideShow.next();
    }

    @FXML
    private void onChangeSpeed(MouseEvent event)
    {
        slideShow.setSpeed((int) sliderSpeed.getValue());
    }
    
    private void setSlideShow(SlideImages slideShow)
    {
        this.slideShow = slideShow;
        Platform.runLater(()->
        {        
            this.imageView.imageProperty().bind(slideShow.currentImageProperty());
            this.labelFileName.textProperty().bind(slideShow.currentFileProperty());
        });
        sliderSpeed.setValue(slideShow.getSpeed());
        slideShow.next();
    }
    
    @FXML
    private void addSlideShow(ActionEvent event)
    {
        if(slideShows.size() < 10)
        {
            slideShows.add(new SlideImages());
            ToggleButton btn = new ToggleButton("SlideShow " + ++slideShowIdx);
            btn.setToggleGroup(tgLeft);
            btn.setOnAction((e)->{
                onSlideShowButton(e);
            });
            vboxLeft.getChildren().add(btn);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(50);
            btnPlus.toFront();
        }
        if(slideShows.size() == 10)
        {
            btnPlus.setVisible(false);
        }
    }
    
    @FXML
    private void onSlideShowButton(ActionEvent event)
    {
        int index = vboxLeft.getChildren().indexOf(event.getSource());
        if(index > -1)
        {
            slideShow.stop();
            setSlideShow(slideShows.get(index));
        }
        
    }

    @FXML
    private void onStartAll(ActionEvent event)
    {
        for(SlideImages slide: slideShows)
        {
            slide.start();
        }
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate( ()->nextSlideShow() , 20, 20, TimeUnit.SECONDS);
        
    }
    
    private void nextSlideShow()
    {
        int index = slideShows.indexOf(slideShow);
        if(index > -1)
        {
            ToggleButton btn = (ToggleButton)vboxLeft.getChildren().get(index);
            index = index != slideShows.size() - 1? index + 1 : 0;
            ToggleButton newBtn = (ToggleButton)vboxLeft.getChildren().get(index); 
            Platform.runLater(()->
            {
                btn.setSelected(false);
                newBtn.setSelected(true);
            });
            setSlideShow(slideShows.get(index));

        }
    }
    
}
