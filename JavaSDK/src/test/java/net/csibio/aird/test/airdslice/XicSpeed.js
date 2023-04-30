option = {
    xAxis: [
        {
            gridIndex:0,
            type: 'category',
            data: ['File1', 'File2', 'File3', 'File4', 'File5', 'File6', 'File7'
                , 'File8', 'File9', 'File10', 'File11', 'File12', 'File13', 'File14', 'File15', 'File16'
            ]
        },
        {
            gridIndex:1,
            type: 'category',
            data: ['File1', 'File2', 'File3', 'File4', 'File5', 'File6', 'File7'
                , 'File8', 'File9', 'File10', 'File11', 'File12', 'File13', 'File14', 'File15', 'File16'
            ]
        }],
    yAxis:[
        {
            gridIndex: 0,
            type: 'value'
        },
        {
            gridIndex: 1,
            type: 'value'
        }],
    legend:[{
        left:'right'
    }],
    title:{
        text:"XIC Speed",
        left:"left"
    },
    grid: [
        {
            top: 50,
            width: '50%',
            bottom: '45%',
            left: 10,
            containLabel: true
        },
        {
            top: '55%',
            width: '50%',
            bottom: 0,
            left: 10,
            containLabel: true
        }
    ],
    series: [
        {
            xAxisIndex: 0,
            yAxisIndex: 0,
            name:"1 target",
            data:[2038,798,325,124,12,420,442,1132,101,255,634,1411,1402,299,247,263],
            type: 'bar',
        },
        {
            xAxisIndex: 0,
            yAxisIndex: 0,
            name:"10 targets",
            data:[2063,808,361,134,17,436,522,1150,109,269,646,1446,1429,294,248,291],
            type: 'bar',
        },
        {
            xAxisIndex: 0,
            yAxisIndex: 0,
            name:"100 targets",
            data:[2438,872,415,146,22,445,544,1312,124,275,691,1568,1660,350,344,344],
            type: 'bar',
        },
        {
            xAxisIndex: 1,
            yAxisIndex: 1,
            name:"1 target",
            data:[11.4857,14.4218,15.5817,5.6279,0.2333,1.443,6.4372,12.5102,0.4427,0.9045,4.4484,14.1759,20.1695,3.0077,3.6081,4.8431],
            type: 'bar',
        },
        {
            xAxisIndex: 1,
            yAxisIndex: 1,
            name:"10 targets",
            data:[527.4482,256.4136,85.8359,31.1905,3.296,25.2543,35.8302,70.6592,4.5187,9.9331,77.975,489.0087,599.2222,87.3544,91.6592,97.6579],
            type: 'bar',
        },
        {
            xAxisIndex: 1,
            yAxisIndex: 1,
            name:"100 targets",
            data:[1558.9392,1886.1907,832.2725,380.9301,21.5816,151.9003,267.7872,657.5839,40.8306,111.2637,390.11,2293.0564,1928.7265,340.3729,341.4002,363.3627],
            type: 'bar',

        }
    ]
};