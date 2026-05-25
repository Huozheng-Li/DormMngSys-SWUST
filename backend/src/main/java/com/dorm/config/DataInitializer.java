package com.dorm.config;

import com.dorm.entity.*;
import com.dorm.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           BuildingRepository buildingRepository,
                           FloorRepository floorRepository,
                           RoomRepository roomRepository,
                           BedRepository bedRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        String encodedPwd = passwordEncoder.encode("123456");

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("系统管理员");
        admin.setPhone("13800000001");
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);

        User dormManager = new User();
        dormManager.setUsername("sg01");
        dormManager.setPassword(encodedPwd);
        dormManager.setName("张宿管");
        dormManager.setPhone("13800000002");
        dormManager.setRole(User.Role.DORM_MANAGER);
        dormManager.setEmployeeId("EMP001");
        userRepository.save(dormManager);

        User counselor = new User();
        counselor.setUsername("fdy01");
        counselor.setPassword(encodedPwd);
        counselor.setName("李辅导员");
        counselor.setPhone("13800000003");
        counselor.setRole(User.Role.COUNSELOR);
        counselor.setEmployeeId("EMP002");
        counselor.setDepartment("计算机学院");
        userRepository.save(counselor);

        String[][] students = {
                {"stu01", "张三", "男", "计科2101", "计算机科学与技术", "2021", "20210101"},
                {"stu02", "李四", "女", "计科2101", "计算机科学与技术", "2021", "20210102"},
                {"stu03", "王五", "男", "计科2101", "计算机科学与技术", "2021", "20210103"},
                {"stu04", "赵六", "女", "软工2101", "软件工程", "2021", "20210201"},
        };
        for (String[] s : students) {
            User stu = new User();
            stu.setUsername(s[0]);
            stu.setPassword(encodedPwd);
            stu.setName(s[1]);
            stu.setGender(s[2]);
            stu.setClassName(s[3]);
            stu.setMajor(s[4]);
            stu.setGrade(s[5]);
            stu.setStudentId(s[6]);
            stu.setRole(User.Role.STUDENT);
            stu.setPhone("1390000" + s[0].substring(4));
            userRepository.save(stu);
        }

        Building building = new Building();
        building.setName("学生公寓A栋");
        building.setAddress("校园南区");
        building.setTotalFloors(6);
        building.setDescription("6层标准学生公寓");
        building = buildingRepository.save(building);

        for (int f = 1; f <= 3; f++) {
            Floor floor = new Floor();
            floor.setBuilding(building);
            floor.setFloorNumber(f);
            floor = floorRepository.save(floor);

            for (int r = 1; r <= 4; r++) {
                Room room = new Room();
                room.setFloor(floor);
                room.setRoomNumber(String.format("%d%02d", f, r));
                room.setRoomType("标准间");
                room.setCapacity(4);
                room.setStatus(Room.RoomStatus.AVAILABLE);
                room = roomRepository.save(room);

                for (int b = 1; b <= 4; b++) {
                    Bed bed = new Bed();
                    bed.setRoom(room);
                    bed.setBedNumber(String.valueOf(b));
                    bed.setStatus(Bed.BedStatus.FREE);
                    bedRepository.save(bed);
                }
            }
        }

        System.out.println("====== 初始数据已创建 ======");
        System.out.println("管理员: admin / admin123");
        System.out.println("宿管: sg01 / 123456");
        System.out.println("辅导员: fdy01 / 123456");
        System.out.println("学生: stu01~04 / 123456");
    }
}
