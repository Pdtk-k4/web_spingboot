package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.Author;
import com.example.bookdahita.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/tacgiaadd")
    public String tacgiaadd(Model model) {
        List<Author> list = this.authorService.getAll();
        model.addAttribute("list", list);

        Author author = new Author();
        model.addAttribute("author", author);
        return "admin/tacgiaadd";
    }

    @PostMapping("/tacgiaadd")
    public String save(@ModelAttribute("category") Author author, Model model) {
        if (this.authorService.create(author)) {
            model.addAttribute("success", "Thêm thành công!");
            return "redirect:/admin/tacgiaadd";
        } else {
            model.addAttribute("error", "Thêm thất bại!");
            model.addAttribute("list", this.authorService.getAll());
            model.addAttribute("category", author);
            return "admin/tacgiaadd";
        }
    }

    @GetMapping("/tacgiaedit/{id}")
    public String tacgiaedit(@PathVariable("id") Long id, Model model) {
        Author author = this.authorService.findById(id);
        if (author == null) {
            return "redirect:/admin/tacgiaadd";
        }
        model.addAttribute("author", author);
        return "admin/tacgiaedit";
    }

    @PostMapping("/tacgiaedit/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("category") Author author, Model model) {
        author.setId(id);
        if (authorService.update(author)) {
            model.addAttribute("success", "Cập nhật thành công!");
            return "redirect:/admin/tacgiaadd";
        } else {
            model.addAttribute("error", "Cập nhật thất bại!");
            model.addAttribute("author", author);
            return "admin/tacgiaedit";
        }
    }

    @GetMapping("/tacgiadel/{id}")
    public String tacgiadel(@PathVariable("id") Long id, Model model) {
        if (authorService.delete(id)) {
            model.addAttribute("success", "Xóa anh mục thành công!");
        } else {
            model.addAttribute("error", "Xóa thất bại!");
        }
        return "redirect:/admin/tacgiaadd";
    }
}
