package blogtest.Service;

import blogtest.Exceptions.ImageNotFoundException;
import blogtest.Model.ImageModel;
import blogtest.Model.Post;
import blogtest.Model.Users;
import blogtest.Repository.ImageRepository;
import blogtest.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageService {
    public static final Logger LOG = LoggerFactory.getLogger(ImageService.class);


    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(UserRepository userRepository,
                        ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public ImageModel getImageToUser(Principal principal) {
        Users users = getUserByPrincipal(principal);
        ImageModel imageModel = imageRepository.findByUserId(users.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel getImageToPost(Long postId) {
        ImageModel imageModel = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Cannot find image to Post: " + postId));
        if (!ObjectUtils.isEmpty(imageModel)) {
            imageModel.setImageBytes(decompressBytes(imageModel.getImageBytes()));
        }
        return imageModel;
    }

    public ImageModel uploadImageToUser(MultipartFile file, Principal principal) throws IOException {
        Users users = getUserByPrincipal(principal);
        LOG.info("Uploading image profile to Users {}", users.getUsername());

        ImageModel userProfileImage = imageRepository.findByUserId(users.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(userProfileImage)) {
            imageRepository.delete(userProfileImage);
        }
        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(users.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setFileName(file.getOriginalFilename());
        return imageRepository.save(imageModel);
    }

    public ImageModel uploadImageToPost(MultipartFile file, Principal principal, Long postId) throws IOException {
        Users users = getUserByPrincipal(principal);
        Post post = users.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());

        ImageModel postProfileImage = imageRepository.findByPostId(postId).orElse(null);
        if (!ObjectUtils.isEmpty(postProfileImage)) {
            imageRepository.delete(postProfileImage);
        }
        ImageModel imageModel = new ImageModel();
        imageModel.setPostId(post.getId());
        imageModel.setImageBytes(compressBytes(file.getBytes()));
        imageModel.setFileName(file.getOriginalFilename());
        LOG.info("Uploading image to Post {}", post.getId());
        return imageRepository.save(imageModel);
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException ex) {
            LOG.error("Cannot compress Bytes");
        }
        System.out.println("Compress Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ex) {
            LOG.error("Cannot decompress Bytes");
        }
        System.out.println("Decompress Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private Users getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUsersByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

    private <T> Collector<T, ?, T> toSinglePostCollector() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }


}
