package com.valentiasoft.backapislackconnector.po

enum MicroTeams {
    DEVOPS('devops'),
    SRE('sre'),
    BACK('back'),
    FRONT('front'),
    UNKNOWN('unknown')

    private final String microTeam

    private MicroTeams(String microTeam){
        this.microTeam = microTeam
    }

    static MicroTeams getTeam(String microTeam){
        return values().find {it ->
            it.toString().equalsIgnoreCase(microTeam)
        } ?: UNKNOWN
    }
}