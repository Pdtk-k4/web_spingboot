package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.Category;
import com.example.bookdahita.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/catadd")
    public String catadd(Model model) {
        //load danh mục
        List<Category> list = this.categoryService.getAllByOrderByIdDesc();
        model.addAttribute("list", list);

        //thêm danh mục
        Category category = new Category();
        category.setCatstatus(true);
        model.addAttribute("category", category);
        return "admin/catadd";
    }

    @PostMapping("/catadd")
    public String save(@ModelAttribute("category") Category category, Model model) {
        if (this.categoryService.create(category)) {
            model.addAttribute("success", "Thêm danh mục thành công!");
            return "redirect:/admin/catadd";
        } else {
            model.addAttribute("error", "Thêm danh mục thất bại!");
            model.addAttribute("list", this.categoryService.getAllByOrderByIdDesc());
            model.addAttribute("category", category);
            return "admin/catadd";
        }
    }

    @GetMapping("/catedit/{id}")
    public String catedit(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.findById(id);
        if (category == null) {
            model.addAttribute("error", "Danh mục không tồn tại!");
            return "redirect:/admin/catadd";
        }
        model.addAttribute("category", category);
        return "admin/catedit";
    }

    @PostMapping("/catedit/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("category") Category category, Model model) {
        category.setId(id);
        if (categoryService.update(category)) {
            model.addAttribute("success", "Cập nhật danh mục thành công!");
            return "redirect:/admin/catadd";
        } else {
            model.addAttribute("error", "Cập nhật danh mục thất bại!");
            model.addAttribute("category", category);
            return "admin/catedit";
        }
    }

    @GetMapping("/catdel/{id}")
    public String catdel(@PathVariable("id") Long id, Model model) {
        if (categoryService.delete(id)) {
            model.addAttribute("success", "Xóa danh mục thành công!");
        } else {
            model.addAttribute("error", "Xóa danh mục thất bại!");
        }
        return "redirect:/admin/catadd";
    }
}
