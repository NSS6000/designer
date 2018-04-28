package com.fr.design.mainframe.backgroundpane;

import com.fr.base.BaseUtils;
import com.fr.base.Style;
import com.fr.base.background.ImageFileBackground;
import com.fr.base.frpx.pack.PictureCollection;
import com.fr.base.frpx.util.ImageIOHelper;
import com.fr.design.constants.UIConstants;
import com.fr.design.border.UIRoundedBorder;
import com.fr.design.event.UIObserverListener;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.mainframe.DesignerContext;
import com.fr.general.Background;
import com.fr.general.Inter;
import com.fr.stable.Constants;
import com.fr.stable.CoreGraphHelper;
import com.fr.design.style.background.image.ImageFileChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author zhou
 * @since 2012-5-29下午1:12:06
 */
public class ImageBackgroundQuickPane extends BackgroundQuickPane {

    private ImagePreviewPane previewPane;
    private Style imageStyle = null;
    private ChangeListener changeListener = null;
    private ImageFileChooser imageFileChooser;

    private UIButtonGroup<Byte> imageLayoutPane;

    private String suffix = PictureCollection.DEFAULT_SUFFIX;

    public ImageBackgroundQuickPane() {
        this(true);
    }

    public ImageBackgroundQuickPane(boolean hasImageLayout) {
        this.setLayout(new BorderLayout(0, 4));
        String[] nameArray = {Inter.getLocText("FR-Background_Image_Default"), Inter.getLocText("FR-Background_Image_Titled"), Inter.getLocText("FR-Background_Image_Adjust"), Inter.getLocText("FR-Background_Image_Extend")};
        Byte[] valueArray = {Constants.IMAGE_CENTER, Constants.IMAGE_TILED, Constants.IMAGE_EXTEND, Constants.IMAGE_ADJUST};
        imageLayoutPane = new UIButtonGroup<Byte>(nameArray, valueArray);
        imageLayoutPane.setSelectedIndex(0);

        previewPane = new ImagePreviewPane();
        JPanel borderPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        borderPane.setBorder(new UIRoundedBorder(UIConstants.LINE_COLOR, 1, 5));
        borderPane.setPreferredSize(new Dimension(0, 145));
        borderPane.add(previewPane, BorderLayout.CENTER);
        this.add(borderPane, BorderLayout.NORTH);
        previewPane.addChangeListener(imageSizeChangeListener);

        JPanel southPane = new JPanel(new BorderLayout(0, 4));
        JPanel contentPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        contentPane.add(southPane, BorderLayout.NORTH);
        this.add(contentPane, BorderLayout.CENTER);


        UIButton selectPictureButton = new UIButton(Inter.getLocText("Image-Select_Picture"));
        selectPictureButton.addActionListener(selectPictureActionListener);

        if (hasImageLayout) {
            southPane.add(imageLayoutPane, BorderLayout.SOUTH);
        }
        southPane.add(selectPictureButton, BorderLayout.CENTER);

        imageLayoutPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                imageStyle = Style.DEFAULT_STYLE.deriveImageLayout(imageLayoutPane.getSelectedItem());
                previewPane.setImageStyle(imageStyle);
                previewPane.repaint();
            }
        });
    }

    /**
     * Select picture.
     */
    ActionListener selectPictureActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (imageFileChooser == null) {
                imageFileChooser = new ImageFileChooser();
                imageFileChooser.setMultiSelectionEnabled(false);
            }
            int returnVal = imageFileChooser.showOpenDialog(DesignerContext.getDesignerFrame());
            if (returnVal != JFileChooser.CANCEL_OPTION) {
                File selectedFile = imageFileChooser.getSelectedFile();

                if (selectedFile != null && selectedFile.isFile()) {
                    String path = selectedFile.getPath();
                    suffix = ImageIOHelper.getSuffix(path);
                    Image image = BaseUtils.readImage(path);
                    CoreGraphHelper.waitForImage(image);

                    previewPane.setImage(image);
                    imageStyle = Style.DEFAULT_STYLE.deriveImageLayout(imageLayoutPane.getSelectedItem());
                    previewPane.setImageStyle(imageStyle);
                    previewPane.repaint();
                } else {
                    previewPane.setImage(null);
                }
                fireChagneListener();
            }

        }
    };

    @Override
    public void populateBean(Background background) {

        ImageFileBackground imageBackground = (ImageFileBackground) background;
        suffix = imageBackground.getSuffix();
        imageLayoutPane.setSelectedItem((byte) imageBackground.getLayout());
        Style.DEFAULT_STYLE.deriveImageLayout(imageBackground.getLayout());

        previewPane.setImageStyle(ImageBackgroundQuickPane.this.imageStyle);
        previewPane.setImage(imageBackground.getImage());
        previewPane.repaint();
    }

    @Override
    public Background updateBean() {
        ImageFileBackground imageBackground = new ImageFileBackground(previewPane.getImage(), suffix);
        imageStyle = Style.DEFAULT_STYLE.deriveImageLayout(imageLayoutPane.getSelectedItem());
        imageBackground.setLayout(imageStyle.getImageLayout());
        return imageBackground;
    }

    /**
     * 给组件登记一个观察者监听事件
     *
     * @param listener 观察者监听事件
     */
    @Override
    public void registerChangeListener(final UIObserverListener listener) {
        changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                listener.doChange();
            }
        };
        imageLayoutPane.addChangeListener(changeListener);
    }


    private void fireChagneListener() {
        if (this.changeListener != null) {
            ChangeEvent evt = new ChangeEvent(this);
            this.changeListener.stateChanged(evt);
        }
    }

    ChangeListener imageSizeChangeListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent evt) {
            if (imageLayoutPane.getSelectedItem() != null) {
                imageStyle = Style.DEFAULT_STYLE.deriveImageLayout(imageLayoutPane.getSelectedItem());
                previewPane.setImageStyle(imageStyle);
                previewPane.repaint();
            }
        }
    };

    /**
     * 判断是否是图片背景
     *
     * @param background 背景
     * @return 判断是否是图片背景
     */
    @Override
    public boolean accept(Background background) {
        return background instanceof ImageFileBackground;
    }

    /**
     * 标题
     *
     * @return 标题
     */
    @Override
    public String title4PopupWindow() {
        return Inter.getLocText("FR-Background_Image");
    }

    @Override
    public void reset() {
        imageLayoutPane.setSelectedIndex(0);
        previewPane.setImage(null);
    }
}