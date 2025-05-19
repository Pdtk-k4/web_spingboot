package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.Slide;
import com.example.bookdahita.service.SlideService;
import com.example.bookdahita.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class SlideController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private SlideService slideService;

    @GetMapping("/sliderlist")
    public String sliderlist(Model model) {
        var slides = slideService.getAll();
        model.addAttribute("slides", slides);
        if (slides.isEmpty()) {
            model.addAttribute("message", "Chưa có slide nào được tạo.");
        }
        return "admin/sliderlist";
    }

    @GetMapping("/slideradd")
    public String slideradd(Model model) {
        Slide slide = new Slide();
        slide.setSlactive(true);
        slide.setSltarget("_self"); // Giá trị mặc định cho sltarget
        model.addAttribute("slide", slide);
        return "admin/slideradd";
    }

    @PostMapping("/slideradd")
    public String save(@ModelAttribute("slide") Slide slide,
                       @RequestParam("slImage") MultipartFile file,
                       Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn một tệp hình ảnh.");
            return "admin/slideradd";
        }

        try {
            this.storageService.store(file);
            String fileName = file.getOriginalFilename();
            slide.setSlimage(fileName);
            if (this.slideService.create(slide)) {
                return "redirect:/admin/sliderlist";
            } else {
                model.addAttribute("error", "Không thể tạo slide. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lưu tệp: " + e.getMessage());
        }
        return "admin/slideradd";
    }

    @GetMapping("/slideredit/{id}")
    public String slideredit(@PathVariable("id") Long id, Model model) {
        Slide slide = slideService.findById(id);
        if (slide == null) {
            return "redirect:/admin/sliderlist";
        }
        model.addAttribute("slide", slide);
        return "admin/slideredit";
    }

    @PostMapping("/slideredit/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("slide") Slide slide,
                         @RequestParam("slImage") MultipartFile file, Model model) {
        Slide existingSlide = slideService.findById(id);
        if (existingSlide == null) {
            return "redirect:/admin/sliderlist";
        }

        // Cập nhật các trường của slide
        existingSlide.setSltitle(slide.getSltitle());
        existingSlide.setSllink(slide.getSllink());
        existingSlide.setSltarget(slide.getSltarget());
        existingSlide.setSlactive(slide.getSlactive());

        // Xử lý hình ảnh mới nếu có
        if (!file.isEmpty()) {
            // Xóa hình ảnh cũ
            if (existingSlide.getSlimage() != null) {
                storageService.delete(existingSlide.getSlimage());
            }
            // Lưu hình ảnh mới
            try {
                storageService.store(file);
                String fileName = file.getOriginalFilename();
                existingSlide.setSlimage(fileName);
            } catch (Exception e) {
                model.addAttribute("error", "Lỗi khi lưu tệp: " + e.getMessage());
                return "admin/slideredit";
            }
        }

        if (slideService.update(existingSlide)) {
            return "redirect:/admin/sliderlist";
        }
        model.addAttribute("error", "Không thể cập nhật slide. Vui lòng thử lại.");
        return "admin/slideredit";
    }

    @GetMapping("/sliderdelete/{id}")
    public String sliderdelete(@PathVariable("id") Long id, Model model) {
        Slide slide = slideService.findById(id);
        if (slide == null) {
            model.addAttribute("message", "Slide không tồn tại.");
            return "redirect:/admin/sliderlist";
        }

        try {
            // Xóa hình ảnh liên quan nếu có
            if (slide.getSlimage() != null && !slide.getSlimage().isEmpty()) {
                storageService.delete(slide.getSlimage());
            }
            // Xóa slide khỏi cơ sở dữ liệu
            if (slideService.delete(id)) {
                model.addAttribute("message", "Xóa slide thành công.");
            } else {
                model.addAttribute("message", "Không thể xóa slide. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Lỗi khi xóa slide: " + e.getMessage());
        }

        return "redirect:/admin/sliderlist";
    }
}