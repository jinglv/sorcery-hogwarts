<template>
  <v-row>
    <v-col cols="6">
      <div id="myChart" style="width: 100%; height: 300px"></div>
    </v-col>
    <v-col cols="6">
      <div id="pie" style="width: 100%; height: 300px"></div>
    </v-col>
  </v-row>
</template>

<script>
export default {
  data() {
    return {
      countX: [],
      countData: [],
      statusX: [],
      statusData: [],
    }
  },
  // 方法调用
  created() {
    this.$api.report.getCaseCount().then(res => {
      let listData
      listData = res.data.data
      for (let i = 0; i < listData.length; i++) {
        // X轴（横轴）绑定测试任务id
        this.countX.push("任务id:" + listData[i].id)
        // 纵轴，绑定测试用例的数量
        this.countData.push(listData[i].caseCount)
      }
      this.drawChart()
    })
    this.$api.report.getStatus().then(res => {
      const listData = res.data.data.taskDataDtoList
      for (let i = 0; i < listData.length; i++) {
        this.statusX.push(listData[i].desc)
        this.statusData.push({value: listData[i].taskCount, name: listData[i].description})
      }
      this.drawPie()
    })
  },
  methods: {
    // 折线图
    drawChart() {
      // 引入echarts
      const myEcharts = require('echarts');
      const myChart = myEcharts.init(document.getElementById("myChart"));
      myChart.setOption({
        title: {
          text: '测试任务用例数量统计'
        },
        xAxis: {
          type: 'category',
          data: this.countX
        },
        yAxis: {
          type: 'value'
        },
        // 坐标轴对应的数据
        series: [{
          data: this.countData,
          type: 'line'
        }]
      });
    },
    // 饼状图
    drawPie() {
      // 引入echarts
      const myEcharts = require('echarts');
      const myChart = myEcharts.init(document.getElementById("pie"));
      myChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: this.statusData
        },
        series: [
          {
            name: '任务类型统计',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.statusData
          }
        ]
      })
    }
  },
}
</script>
