package com.valentiasoft.backapislackconnector.services

import com.valentiasoft.backapislackconnector.components.MicroProperties
import com.valentiasoft.backapislackconnector.po.MicroTeams
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MicroService {

    private final MicroProperties props

    @Autowired
    MicroService(MicroProperties props){
        this.props = props
    }

    private String validateName(){
        return props.name
    }

    private String validateVersion(){
        if(props.version ==~ '[0-9]+\\.[0-9]+\\.[0-9]') {
            return props.version
        } else {
            return 'Invalid micro version, use regex [0-9]+\\.[0-9]+\\.[0-9]+ (ex: 0.0.1)'
        }
    }

    private String validateTeam(){
        return MicroTeams.getTeam(props.team)
    }

    MicroProperties getMicroEntity(){
        props.name = validateName()
        props.version = validateVersion()
        props.team = validateTeam()

        return props
    }
}
