package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.repository.HDNhapHangChiTietRepository;
import com.example.bookdahita.service.HDNhapHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HDNhapHangServiceImpl implements HDNhapHangService {

    @Autowired
    private HDNhapHangChiTietRepository hdnhapHangChiTietRepository;

    @Override
    public Boolean delete(Long id) {
        try {
            this.hdnhapHangChiTietRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
