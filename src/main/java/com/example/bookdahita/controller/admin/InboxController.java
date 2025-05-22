package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.DetailInbox;
import com.example.bookdahita.models.Inbox;
import com.example.bookdahita.repository.DetailInboxRepository;
import com.example.bookdahita.repository.InboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class InboxController {

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private DetailInboxRepository detailInboxRepository;

    @GetMapping("/inbox")
    public String inbox(Model model) {
        List<Inbox> inboxList = inboxRepository.findAllByOrderByDateDesc();
        model.addAttribute("inboxList", inboxList != null ? inboxList : new ArrayList<>());
        return "admin/inbox";
    }

    @GetMapping("/detail_inbox")
    public String detailInbox(@RequestParam("id") Long inboxId, Model model) {
        try {
            Inbox inbox = inboxRepository.findById(inboxId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
            List<DetailInbox> detailInboxList = detailInboxRepository.findByInboxId(inboxId);

            // Tính tổng số lượng và tổng tiền từ DetailInbox
            int totalQuantity = detailInboxList.stream()
                    .mapToInt(DetailInbox::getQuantity)
                    .sum();
            int totalPrice = detailInboxList.stream()
                    .mapToInt(detail -> {
                        int price = detail.getProduct().getProprice();
                        int sale = detail.getProduct().getProsale();
                        int finalPrice = sale > 0 ? price - (price * sale / 100) : price;
                        return finalPrice * detail.getQuantity();
                    })
                    .sum();

            model.addAttribute("inbox", inbox);
            model.addAttribute("detailInboxList", detailInboxList);
            model.addAttribute("totalQuantity", totalQuantity);
            model.addAttribute("totalPrice", totalPrice);
            return "admin/detail_inbox";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/detail_inbox";
        }
    }

    @PostMapping("/approve_inbox")
    public String approveInbox(@RequestParam("inboxId") Long inboxId, Model model) {
        try {
            Inbox inbox = inboxRepository.findById(inboxId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
            inbox.setStatus(true); // Cập nhật trạng thái thành Đã duyệt
            inboxRepository.save(inbox);
            return "redirect:/admin/inbox";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/detail_inbox";
        }
    }
}
