package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Slide;
import com.example.bookdahita.repository.SlideRepository;
import com.example.bookdahita.service.SlideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlideServiceImpl implements SlideService {

    @Autowired
    private SlideRepository slideRepository;

    @Override
    public List<Slide> getAll() {
        return this.slideRepository.findAll();
    }

    @Override
    public Boolean create(Slide slide) {
        try {
            this.slideRepository.save(slide);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Slide findById(Long id) {
        return this.slideRepository.findById(id).get();
    }

    @Override
    public Boolean update(Slide slide) {
        try {
            this.slideRepository.save(slide);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            this.slideRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public List<Slide> getActiveSlidesLimitedToFour() {
        return slideRepository.findAll()
                .stream()
                .filter(slide -> slide.getSlactive() != null && slide.getSlactive())
                .limit(5)
                .collect(Collectors.toList());
    }
}
