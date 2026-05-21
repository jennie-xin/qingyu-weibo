package com.qingyu.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/placeholder")
public class PlaceholderController {

    private static final Color[] COLORS = {
        new Color(242, 167, 176),
        new Color(168, 216, 234),
        new Color(195, 177, 225),
        new Color(181, 234, 215),
        new Color(255, 234, 167),
        new Color(162, 217, 206),
        new Color(255, 183, 178),
        new Color(199, 206, 234),
        new Color(255, 218, 185),
        new Color(186, 220, 188)
    };

    @GetMapping("/{width}x{height}")
    public ResponseEntity<byte[]> generate(
            @PathVariable int width,
            @PathVariable int height,
            @RequestParam(defaultValue = "0") int seed,
            @RequestParam(defaultValue = "") String text) {
        try {
            width = Math.min(width, 1200);
            height = Math.min(height, 900);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color baseColor = COLORS[Math.abs(seed) % COLORS.length];
            Color darkerColor = baseColor.darker();

            GradientPaint gradient = new GradientPaint(0, 0, baseColor, width, height, darkerColor);
            g.setPaint(gradient);
            g.fillRect(0, 0, width, height);

            if (!text.isEmpty()) {
                g.setColor(new Color(255, 255, 255, 200));
                g.setFont(new Font("SansSerif", Font.BOLD, Math.min(width, height) / 8));
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g.drawString(text, (width - textWidth) / 2, (height + textHeight / 2) / 2);
            }

            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Cache-Control", "public, max-age=86400")
                .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
