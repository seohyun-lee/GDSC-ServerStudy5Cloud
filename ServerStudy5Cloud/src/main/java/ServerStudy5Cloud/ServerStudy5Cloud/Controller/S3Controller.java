package ServerStudy5Cloud.ServerStudy5Cloud.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class S3Controller {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @GetMapping("/")
    public String listFiles(Model model) {
        // S3 버킷의 객체 목록 가져오기
        List<S3ObjectSummary> objectSummaries = amazonS3.listObjectsV2(bucketName).getObjectSummaries();
        // getUrl로 객체 URL 가져온 후, List<String>에 넣어 index.html에 반환
        List<String> fileUrls = new ArrayList<>();
        for (S3ObjectSummary os : objectSummaries) {
            String url = amazonS3.getUrl(bucketName, os.getKey()).toString();
            fileUrls.add(url);
        }
        model.addAttribute("fileUrls", fileUrls);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        // putObject로 파일을 S3 버킷에 업로드
        amazonS3.putObject(bucketName, filename, file.getInputStream(), null);
        // ACL 퍼블릭으로 설정
        amazonS3.setObjectAcl(bucketName, filename, CannedAccessControlList.PublicRead);
        return "redirect:/";
    }
}
// 동작 영상 : https://drive.google.com/file/d/10wPjsBbnbIR5E6pKbwmr2sZ-GsLY-FWP/view?usp=sharing