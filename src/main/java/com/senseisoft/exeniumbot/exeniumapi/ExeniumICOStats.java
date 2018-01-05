package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ExeniumICOStats implements Serializable {

    @JsonProperty("sold_xnt")
    public BigDecimal soldXNT;
    @JsonProperty("bounty_xntb")
    public BigDecimal bountyXNTB;
    @JsonProperty("welcome_xnt")
    public BigDecimal welcomeXNT;
    @JsonProperty("welcome_xntb")
    public BigDecimal welcomeXNTB;
    public BigDecimal contributors;
    @JsonProperty("total_emitted")
    public BigDecimal totalEmitted;

    @JsonProperty("referal_xntb")
    public BigDecimal referalXNTB;
    @JsonProperty("stage_xntb")
    public BigDecimal stageXNTB;

    @JsonProperty("team_volume_xnt")
    public BigDecimal teamXNT;

    @JsonProperty("marketing_volume_xnt")
    public BigDecimal marketingXNT;

    public ExeniumICOStats() {
    }
}
