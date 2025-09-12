package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Role;
import com.example.bookdahita.models.User;
import com.example.bookdahita.models.UserRole;
import com.example.bookdahita.repository.RoleRepository;
import com.example.bookdahita.repository.UserRepository;
import com.example.bookdahita.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public void registerNewUser(User user) throws Exception {
        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByUserName(user.getEmail()) != null) {
            throw new Exception("Email đã tồn tại");
        }

        // Mã hóa mật khẩu
        user.setPass(passwordEncoder.encode(user.getPass()));
        user.setUserName(user.getEmail()); // Sử dụng email làm username

        // Đặt giá trị rỗng cho phone và address nếu không được cung cấp
        if (user.getPhone() == null) {
            user.setPhone("");
        }
        if (user.getAddress() == null) {
            user.setAddress("");
        }

        user.setCreatedDate(LocalDateTime.now());

        // Gán vai trò mặc định "USER"
        Role userRole = roleRepository.findByNameRole("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setNameRole("USER");
            roleRepository.save(userRole);
        }

        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUser(user);
        userRoleEntity.setRole(userRole);

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRoleEntity);
        user.setUserRoles(userRoles);

        // Lưu người dùng vào cơ sở dữ liệu
        userRepository.save(user);
    }

    @Override
    public void updateUser(User user) throws Exception {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            throw new Exception("Người dùng không tồn tại");
        }

        // Cập nhật các trường cần thiết, không động đến mật khẩu
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());

        // Kiểm tra email trùng lặp (nếu cần)
        if (!existingUser.getEmail().equals(user.getEmail()) && userRepository.findByUserName(user.getEmail()) != null) {
            throw new Exception("Email đã tồn tại");
        }

        userRepository.save(existingUser);
    }
}