package digibridge;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PDFPanel extends JPanel {
    private BufferedImage combinedImage;

    public PDFPanel() {
        super();
    }

    public void setCombinedImage(BufferedImage combinedImage) {
        this.combinedImage = combinedImage;
        repaint(); // Repaint the panel to reflect the changes
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (combinedImage != null) {
            g.drawImage(combinedImage, 0, 0, this); // Draw the image at (0, 0) with panel dimensions
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (combinedImage != null) {
            return new Dimension(combinedImage.getWidth(), combinedImage.getHeight());
        }
        return super.getPreferredSize();
    }
    
    public void clearPanel() {
        this.combinedImage = null; // Set combinedImage to null
        this.revalidate(); // Revalidate the panel
        this.repaint(); // Repaint the panel
    }
}
