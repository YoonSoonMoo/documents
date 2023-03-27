package kr.pe.yoonsm.ehcache.controller;

import kr.pe.yoonsm.ehcache.service.CacheReplicationService;
import kr.pe.yoonsm.ehcache.service.CallMappingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheReplicationService cacheReplicationService;

    @RequestMapping(value = "/call/save", method = {RequestMethod.POST})
    public String saveCallMapping(@RequestBody CallMappingDto callMappingDto) {
        cacheReplicationService.setCallInfo(callMappingDto);
        return "메모리에 콜매핑 정보를 저장했습니다.";
    }


    @RequestMapping(value = "/call/read/no/{seq}", method = {RequestMethod.GET})
    public CallMappingDto getCallMapping(@PathVariable("seq") String seq) {
        log.info("-- call getCallMapping");
        return cacheReplicationService.getCallInfo(seq);
    }

    /**
     * Repository에 있는 데이타를 모두 가져온다.
     *
     * @return
     */
    @RequestMapping(value = "/call/read/all", method = {RequestMethod.GET})
    public List<CallMappingDto> getAllCallMapping() {
        return cacheReplicationService.getAllCallInfo();
    }

    @RequestMapping(value = "/call/clear", method = {RequestMethod.GET})
    public String clearCache() {
        cacheReplicationService.clearCache();
        log.info("All cache are deleted!!");
        return "모든 캐시가 삭제 되었습니다.!!";
    }

    @RequestMapping(value = "/call/clear/{seq}", method = {RequestMethod.GET})
    public String clearCache(@PathVariable("seq") String seq) {
        cacheReplicationService.clearCache(seq);
        log.info("{} cache are deleted!!", seq);
        return "캐시가 삭제 되었습니다.!!";
    }


    /**
     * 특정 이벤트가 발생하기 전까지 대기를 한다. ( /v2/call/kick )
     */
    @RequestMapping(value = "/v2/call/save", method = {RequestMethod.POST})
    public String saveCallMappingWait(@RequestBody CallMappingDto callMappingDto) {
        cacheReplicationService.callOtherService(callMappingDto);
        return "메모리에 콜매핑 정보를 저장했습니다.";
    }

    /**
     * 이벤트를 발생시킨다.
     * /v2/call/save 에서 중단되어 있는 처리를 릴리스 한다.
     *
     * @return
     */
    @RequestMapping(value = "/v2/call/kick/{seq}", method = {RequestMethod.GET})
    public String eventTrigger(@PathVariable("seq") String seq) {
        cacheReplicationService.eventPublish(seq);
        return "이벤트 발생!!";
    }
}
