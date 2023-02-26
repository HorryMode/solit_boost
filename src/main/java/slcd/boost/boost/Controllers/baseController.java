package slcd.boost.boost.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1")
public class baseController {
    @GetMapping("/info")
    public String getBaseInfo(){
        return "базовая информация";
    }
}
