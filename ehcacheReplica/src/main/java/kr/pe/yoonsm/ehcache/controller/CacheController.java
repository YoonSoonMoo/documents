package kr.pe.yoonsm.ehcache.controller;

import kr.pe.yoonsm.ehcache.service.CacheReplicationService;
import kr.pe.yoonsm.ehcache.service.CallMappingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheReplicationService cacheReplicationService;

    @GetMapping("replication")
    public Integer replication() {
        return this.cacheReplicationService.getCachedReplicationValue();
    }

    @GetMapping("increase")
    public Integer increase() {
        return this.cacheReplicationService.getReplicationValue();
    }

    @ResponseBody
    @RequestMapping(value = "/call/save", method = {RequestMethod.POST})
    public String saveCallMapping(@RequestBody CallMappingDto callMappingDto) {
        cacheReplicationService.setCallInfo(callMappingDto);
        return "메모리에 콜매핑 정보를 저장했습니다.";
    }

    @ResponseBody
    @RequestMapping(value = "/call/read", method = {RequestMethod.GET})
    public CallMappingDto getCallMapping() {
        return cacheReplicationService.getCallInfo();
    }


}
