<?xml version="1.0" encoding="UTF-8" ?>
<PlaybillLists>
    <Playbill>
        <PlayDay><${key!''}></PlayDay>
        <PlayLists>
            <#list epgs as epg>
                <Play>
                    <StartTime><![CDATA[${epg.beginTime!''}]]></StartTime>
                    <EndTime><![CDATA[${epg.endTime!''}]]></EndTime>
                    <PlayName><![CDATA[${epg.programName!''}]]></PlayName>
                </Play>
            </#list>
        </PlayLists>
    </Playbill>
</PlaybillLists>