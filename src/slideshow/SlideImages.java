/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slideshow;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

/**
 *
 * @author Marek
 */
public class SlideImages
{
    private int speed;
    private List<Pair<String,Image>> imageList;
    private ScheduledExecutorService executor;
    private final ObjectProperty<Image> currentImage = new SimpleObjectProperty<>();
    private final StringProperty currentFile = new SimpleStringProperty();

    public String getCurrentFile()
    {
        return currentFile.get();
    }

    public StringProperty currentFileProperty()
    {
        return currentFile;
    }
    
    public SlideImages()
    {
        imageList = new ArrayList<Pair<String,Image>>();
        imageList.add(new Pair("File name",new Image("/slideshow/default.png")));
        setImageList(imageList);
        speed = 1;
    }
    
    public Image getCurrentImage()
    {
        return currentImage.get();
    }

    public ObjectProperty currentImageProperty()
    {
        return currentImage;
    }
    
    public void next()
    {
        if(!imageList.isEmpty())
        {
            int index = getIndex();
            if(index < imageList.size() - 1)
                index++;
            else
                index = 0;
            setIndex(index);
        }
    }
    
    public void prev()
    {
        if(!imageList.isEmpty())
        {
            int index = getIndex();
            if(index > 0)
                index--;
            else
                index = imageList.size() - 1;
            setIndex(index);
        } 
    }    
    
    private int getIndex()
    {
        return imageList.indexOf(new Pair(currentFile.get(),currentImage.get()));
    }
    
    private void setIndex(int index)
    {
        Pair<String,Image> p = imageList.get(index);
        Platform.runLater(()->
        {        
            currentImage.set(p.getValue());
            currentFile.set(p.getKey());
        });
    }
    
    
    public void start()
    {
        Runnable thread = new Runnable()
        {
            @Override
            public void run()
            {
                next();
            }
        };
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(thread, speed, speed, TimeUnit.SECONDS);

    }
    
    public void stop()
    {
        if(executor != null && !executor.isShutdown())
            executor.shutdown();
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
        if(executor != null && !executor.isShutdown())
        {
            stop();
            start();
        }
    }

    public List<Pair<String,Image>> getImageList()
    {
        return imageList;
    }

    public void setImageList(List<Pair<String,Image>> imageList)
    {
        this.imageList = imageList;
        setIndex(0);
    }
    
    
}
