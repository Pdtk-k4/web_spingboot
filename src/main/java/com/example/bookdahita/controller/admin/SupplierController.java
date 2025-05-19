package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.Supplier;
import com.example.bookdahita.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class SupplierController {

    @Autowired
    private SupplierService  supplierService;

    @GetMapping("/nhacungcapadd")
    public String nhacungcapadd(Model model) {
        List<Supplier> list = this.supplierService.getAll();
        model.addAttribute("list", list);

        Supplier supplier = new Supplier();
        model.addAttribute("supplier", supplier);
        return "admin/nhacungcapadd";
    }

    @PostMapping("/nhacungcapadd")
    public String save(@ModelAttribute("supplier") Supplier supplier, Model model) {
        if (this.supplierService.create(supplier)) {
            model.addAttribute("success", "Thêm thành công!");
            return "redirect:/admin/nhacungcapadd";
        } else {
            model.addAttribute("error", "Thêm thất bại!");
            model.addAttribute("list", this.supplierService.getAll());
            model.addAttribute("supplier", supplier);
            return "admin/nhacungcapadd";
        }
    }

    @GetMapping("/nhacungcapedit/{id}")
    public String nhacungcapedit(@PathVariable("id") Long id, Model model) {
        Supplier supplier = this.supplierService.findById(id);
        if (supplier == null) {
            return "redirect:/admin/nhacungcapadd";
        }
        model.addAttribute("supplier", supplier);
        return "admin/nhacungcapedit";
    }

    @PostMapping("/nhacungcapedit/{id}")
    public String update(@PathVariable("id") Long id, @ModelAttribute("supplier") Supplier supplier, Model model) {
        supplier.setId(id);
        if (supplierService.update(supplier)) {
            model.addAttribute("success", "Cập nhật thành công!");
            return "redirect:/admin/nhacungcapadd";
        } else {
            model.addAttribute("error", "Cập nhật thất bại!");
            model.addAttribute("supplier", supplier);
            return "admin/nhacungcapedit";
        }
    }

    @GetMapping("/nccdel/{id}")
    public String catdel(@PathVariable("id") Long id, Model model) {
        if (supplierService.delete(id)) {
            model.addAttribute("success", "Xóa anh mục thành công!");
        } else {
            model.addAttribute("error", "Xóa thất bại!");
        }
        return "redirect:/admin/nhacungcapadd";
    }
}
