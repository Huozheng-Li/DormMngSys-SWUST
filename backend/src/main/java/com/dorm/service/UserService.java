package com.dorm.service;

import com.dorm.entity.User;
import com.dorm.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);
    }

    public User update(Long id, User updated) {
        User user = findById(id);
        user.setName(updated.getName());
        user.setPhone(updated.getPhone());
        user.setEmail(updated.getEmail());
        user.setGender(updated.getGender());
        user.setClassName(updated.getClassName());
        user.setMajor(updated.getMajor());
        user.setGrade(updated.getGrade());
        user.setStudentId(updated.getStudentId());
        user.setEmployeeId(updated.getEmployeeId());
        user.setDepartment(updated.getDepartment());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> search(String keyword) {
        return userRepository.findByStudentIdContainingOrNameContaining(keyword, keyword);
    }
}
