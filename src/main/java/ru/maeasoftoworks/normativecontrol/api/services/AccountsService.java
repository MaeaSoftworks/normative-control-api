package ru.maeasoftoworks.normativecontrol.api.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.users.*;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDocumentsLimitDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserEmailDto;
import ru.maeasoftoworks.normativecontrol.api.dto.accounts.UpdateUserPasswordDto;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.AuthJwtPair;
import ru.maeasoftoworks.normativecontrol.api.exceptions.FieldNotPresents;
import ru.maeasoftoworks.normativecontrol.api.exceptions.PasswordsMismatchException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceAlreadyExistsException;
import ru.maeasoftoworks.normativecontrol.api.exceptions.ResourceNotFoundException;
import ru.maeasoftoworks.normativecontrol.api.repositories.*;
import ru.maeasoftoworks.normativecontrol.api.utils.hashing.Sha256;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountsService {
    private final UsersRepository usersRepository;
    private final AcademicGroupsRepository academicGroupsRepository;
    private final DocumentsRepository documentsRepository;
    private final ResultsRepository resultsRepository;
    private final NormocontrollersRepository normocontrollersRepository;
    private final JwtService jwtService;

    // Доступна только админу
    public List<User> getUsers() {
        return usersRepository.findAll();
    }

    public List<Normocontroller> getNormocontrollers() {
        return normocontrollersRepository.findAll();
    }

    // Доступна админу - для всех аккаунтов
    // Остальным - только для своего
    public User getUserById(Long targetId) {
        User target = usersRepository.findUsersById(targetId);

        if (target == null) {
            String message = MessageFormat.format("User with id {0} not found", targetId);
            throw new ResourceNotFoundException(message);
        }

        return target;
    }

    @Transactional
    public User updateUserById(Long userId, UpdateUserDto updateUserDto) {
        User user = usersRepository.findUsersById(userId);

        if (user == null) {
            String message = MessageFormat.format("User with id {0} not found", userId);
            throw new ResourceNotFoundException(message);
        }

        user.setFullName(updateUserDto.getFullName());

        if (user.getRole() == Role.STUDENT) {
            Student student = (Student) user;
            AcademicGroup academicGroup = null;
            if (updateUserDto.getAcademicGroupId() != null)
                academicGroup = academicGroupsRepository.findAcademicGroupById(updateUserDto.getAcademicGroupId());
            student.setAcademicGroup(academicGroup);
            usersRepository.save(student);
            return student;
        }

        return user;
    }

    @Transactional
    public void deleteUserById(Long userId) {
        User user = usersRepository.findUsersById(userId);

        if (user == null) {
            String message = MessageFormat.format("User with id {0} not found", userId);
            throw new ResourceNotFoundException(message);
        }

        if (user.getRole() == Role.NORMOCONTROLLER) {
            List<AcademicGroup> academicGroups = academicGroupsRepository.findAcademicGroupsByNormocontrollerId(user.getId());
            for (AcademicGroup academicGroup : academicGroups) {
                academicGroup.setNormocontroller(null);
                academicGroupsRepository.save(academicGroup);
            }
        }

        if (user.getRole() == Role.STUDENT) {
            List<Document> documents = documentsRepository.findDocumentsByStudentId(user.getId());
            List<Result> results = resultsRepository.findResultsByDocument_StudentId(user.getId());
            documentsRepository.deleteAll(documents);
            resultsRepository.deleteAll(results);
        }

        usersRepository.delete(user);
    }

    @Transactional
    public AuthJwtPair updateUserEmailById(Long userId, UpdateUserEmailDto updateUserEmailDto) {
        User user = usersRepository.findUsersById(userId);
        if (user == null) {
            String message = MessageFormat.format("User with id {0} not found", userId);
            throw new ResourceNotFoundException(message);
        }
        if (usersRepository.existsUserByEmail(updateUserEmailDto.getEmail())) {
            String message = MessageFormat.format("User with email {0} already exists", updateUserEmailDto.getEmail());
            throw new ResourceAlreadyExistsException(message);
        }
        user.setEmail(updateUserEmailDto.getEmail());
        usersRepository.save(user);
        return new AuthJwtPair(jwtService.generateAccessTokenForUser(user).getCompactToken(),
                jwtService.generateRefreshTokenForUser(user).getCompactToken());
    }

    @Transactional
    public User updateUserPasswordById(Long userId, UpdateUserPasswordDto updateUserPasswordDto) {
        User user = usersRepository.findUsersById(userId);
        if (user == null) {
            String message = MessageFormat.format("User with id {0} not found", userId);
            throw new ResourceNotFoundException(message);
        }
        if (!user.getPassword().equals(Sha256.getStringSha256(updateUserPasswordDto.getOldPassword()))) {
            throw new PasswordsMismatchException("Old password is wrong");
        }
        user.setPassword(Sha256.getStringSha256(updateUserPasswordDto.getNewPassword()));
        usersRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUserPasswordByIdAsAdmin(Long userId, UpdateUserPasswordDto updateUserPasswordDto) {
        User user = usersRepository.findUsersById(userId);
        if (user == null) {
            String message = MessageFormat.format("User with id {0} not found", userId);
            throw new ResourceNotFoundException(message);
        }
        user.setPassword(Sha256.getStringSha256(updateUserPasswordDto.getNewPassword()));
        usersRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUserDocumentsLimitById(Long userId, UpdateUserDocumentsLimitDto updateUserDocumentsLimitDto) {
        User user = usersRepository.findUsersById(userId);
        if (user == null) {
            String message = MessageFormat.format("User with id {0} not found", userId);
            throw new ResourceNotFoundException(message);
        }
        if (user.getRole() == Role.STUDENT) {
            Student student = (Student) user;
            student.setDocumentsLimit(updateUserDocumentsLimitDto.getDocumentsLimit());
            usersRepository.save(student);
            return student;
        } else {
            throw new FieldNotPresents("This user doesn't have «documents limit» field");
        }
    }
}
