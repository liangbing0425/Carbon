package com.carbon.controller;

import com.carbon.DTO.EmissionReport.ElectrolyticAluminum;
import com.carbon.DTO.Response;
import com.carbon.entity.Emission;
import com.carbon.service.EmissionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emission")
public class EmissionController {

    @Autowired
    private EmissionService emissionService;
    @PostMapping("/EAl")
    public String calculateEmission(@RequestBody ElectrolyticAluminum electrolyticAluminum) throws JsonProcessingException {

        // 计算每种燃料数据
        for (ElectrolyticAluminum.FuelData fuelData : electrolyticAluminum.fuelList) {
            // 计算活动水平数据
            fuelData.activityData= fuelData.ncv * fuelData.fuelConsumption;
            // 计算排放因子数据
            fuelData.emissionFactor = (fuelData.carbonContent * fuelData.oxidationFactor * 44) / 12;
            // 计算燃烧排放
            electrolyticAluminum.eCombustion += fuelData.activityData * fuelData.emissionFactor;
        }

        //计算能源作为原材料用途的排放
        electrolyticAluminum.eMaterial=electrolyticAluminum.siliconFurnaceProduction*electrolyticAluminum.emissionFactorSiliconFurnace;

        //计算工业生产过程排放
        electrolyticAluminum.emissionFactorLimeBurning=electrolyticAluminum.DX*0.478;
        electrolyticAluminum.eProcess=electrolyticAluminum.emissionFactorLimeBurning*electrolyticAluminum.limeConsumption;

        //计算净购入的电力、热力消费的排放
        electrolyticAluminum.netElectricityConsumption=electrolyticAluminum.EpurchaseQuantity-electrolyticAluminum.EexportQuantity;
        electrolyticAluminum.netHeatConsumption=electrolyticAluminum.HpurchaseQuantity-electrolyticAluminum.HexportQuantity;
        electrolyticAluminum.eElectricityHeat=electrolyticAluminum.netElectricityConsumption*electrolyticAluminum.emissionFactorElectricity
                                              +electrolyticAluminum.netHeatConsumption*electrolyticAluminum.emissionFactorHeat;


        // 累加总排放量
        electrolyticAluminum.eTotal = electrolyticAluminum.eCombustion+ electrolyticAluminum.eMaterial+
                                      electrolyticAluminum.eProcess+electrolyticAluminum.eElectricityHeat;


        // 使用ObjectMapper转换为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(electrolyticAluminum);

        //将报告上传至审核
        emissionService.finishEmission(json,"电解铝生产");
        return json;
    }

    @GetMapping("/status")
    public Emission selectByStatus(@RequestParam int status){
        return emissionService.selectByStatus(status);
    }

    @GetMapping("/type")
    public Emission selectByStatus(@RequestParam String type){
        return emissionService.selectByType(type);
    }

    @GetMapping("/updateStatus")
    public Response updateStatus(@RequestParam int id,@RequestParam int status){
        emissionService.updateStatus(id,status);
        if (status==1){
            return new Response().success(null,"已将报告上链");
        }
        if (status==2){
            return new Response().fail(null,"报告有误，被驳回");
        }
        return new Response().fail(null,"未响应");
    }
}

