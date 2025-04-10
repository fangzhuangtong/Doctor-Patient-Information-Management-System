import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 登录页面路由
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login/index.vue'),
      meta: { title: '登录' }
    },
    // 病人端路由
    {
      path: '/patient',
      component: () => import('../views/Patient/PatientSystem.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/Patient/Home.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'personal-info',
          name: 'personalInfo',
          component: () => import('../views/Patient/PersonalInfo.vue'),
          meta: { title: '个人信息' }
        },
        {
          path: 'health-card',
          name: 'healthCard',
          component: () => import('../views/Patient/HealthCard.vue'),
          meta: { title: '健康卡' }
        },
        {
          path: 'online-consultation',
          name: 'onlineConsultation',
          component: () => import('../views/Patient/OnlineConsultation.vue'),
          meta: { title: '在线咨询' }
        },
        {
          path: 'account-binding',
          name: 'accountBinding',
          component: () => import('../views/Patient/AccountBinding.vue'),
          meta: { title: '账号关联' }
        },
        {
          path: 'outpatient',
          name: 'outpatient',
          meta: { title: '门诊服务' },
          children: [
            {
              path: 'appointment',
              name: 'outpatientAppointment',
              component: () => import('../views/Patient/outpatient/Appointment.vue'),
              meta: { title: '预约挂号' }
            },
            {
              path: 'records',
              name: 'outpatientRecords',
              component: () => import('../views/Patient/outpatient/Records.vue'),
              meta: { title: '挂号记录' }
            },
            {
              path: 'reports',
              name: 'outpatientReports',
              component: () => import('../views/Patient/outpatient/Reports.vue'),
              meta: { title: '门诊报告查询' }
            },
            {
              path: 'check-appointment',
              name: 'checkAppointment',
              component: () => import('../views/Patient/outpatient/CheckAppointment.vue'),
              meta: { title: '检查预约' }
            },
            {
              path: 'check-records',
              name: 'checkRecords',
              component: () => import('../views/Patient/outpatient/CheckRecords.vue'),
              meta: { title: '检查记录' }
            },
            {
              path: 'check-reports',
              name: 'checkReports',
              component: () => import('../views/Patient/outpatient/CheckReports.vue'),
              meta: { title: '检查报告查询' }
            }
          ]
        },
        {
          path: 'inpatient',
          name: 'inpatient',
          meta: { title: '住院服务' },
          children: [
            {
              path: 'records',
              name: 'inpatientRecords',
              component: () => import('../views/Patient/inpatient/Records.vue'),
              meta: { title: '住院记录' }
            },
            {
              path: 'reports',
              name: 'inpatientReports',
              component: () => import('../views/Patient/inpatient/Reports.vue'),
              meta: { title: '住院报告查询' }
            },
            {
              path: 'surgery',
              name: 'surgeryInfo',
              component: () => import('../views/Patient/inpatient/Surgery.vue'),
              meta: { title: '手术信息查询' }
            }
          ]
        },
        {
          path: 'physical',
          name: 'physical',
          meta: { title: '体检服务' },
          children: [
            {
              path: 'appointment',
              name: 'physicalAppointment',
              component: () => import('../views/Patient/physical/Appointment.vue'),
              meta: { title: '体检预约' }
            },
            {
              path: 'records',
              name: 'physicalRecords',
              component: () => import('../views/Patient/physical/Records.vue'),
              meta: { title: '体检记录' }
            },
            {
              path: 'reports',
              name: 'physicalReports',
              component: () => import('../views/Patient/physical/Reports.vue'),
              meta: { title: '体检报告查询' }
            }
          ]
        },
        {
          path: 'payment',
          name: 'payment',
          meta: { title: '缴费窗口' },
          children: [
            {
              path: 'outpatient',
              name: 'outpatientPayment',
              component: () => import('../views/Patient/payment/Outpatient.vue'),
              meta: { title: '门诊缴费' }
            },
            {
              path: 'inpatient',
              name: 'inpatientPayment',
              component: () => import('../views/Patient/payment/Inpatient.vue'),
              meta: { title: '住院缴费' }
            },
            {
              path: 'physical',
              name: 'physicalPayment',
              component: () => import('../views/Patient/payment/Physical.vue'),
              meta: { title: '体检缴费' }
            },
            {
              path: 'exam',
              name: 'examPayment',
              component: () => import('../views/Patient/payment/Exam.vue'),
              meta: { title: '检查缴费' }
            }
          ]
        },
        {
          path: 'deepseek',
          name: 'deepseek',
          meta: { title: 'Deepseek' },
          children: [
            {
              path: 'basic',
              name: 'basicModel',
              component: () => import('../views/Patient/deepseek/BasicModel.vue'),
              meta: { title: '基础模型' }
            },
            {
              path: 'r1',
              name: 'r1Model',
              component: () => import('../views/Patient/deepseek/R1Model.vue'),
              meta: { title: 'R1模型' }
            },
            {
              path: 'text',
              name: 'textExtract',
              component: () => import('../views/Patient/deepseek/TextExtract.vue'),
              meta: { title: '文字提取' }
            },
            {
              path: 'local',
              name: 'PatientDeepseekLocal',
              component: () => import('@/views/Patient/deepseek/LocalModel.vue'),
              meta: {
                title: '本地模型',
                requiresAuth: true
              }
            }
          ]
        }
      ]
    },

    // 医生端路由
    {
      path: '/doctor',
      component: () => import('../views/Doctor/DoctorSystem.vue'),
      children: [
        {
          path: '',
          name: 'doctorHome',
          component: () => import('../views/Doctor/Home.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'personal-info',
          name: 'doctorPersonalInfo',
          component: () => import('../views/Doctor/PersonalInfo.vue'),
          meta: { title: '个人信息' }
        },
        {
          path: 'consultation',
          name: 'consultation',
          component: () => import('../views/Doctor/Consultation.vue'),
          meta: { title: '咨询服务' }
        },
        {
          path: 'patient',
          name: 'patient',
          meta: { title: '病人管理' },
          children: [
            {
              path: 'outpatient',
              name: 'patientOutpatient',
              component: () => import('../views/Doctor/patient/Outpatient.vue'),
              meta: { title: '门诊病人' }
            },
            {
              path: 'inpatient',
              name: 'patientInpatient',
              component: () => import('../views/Doctor/patient/Inpatient.vue'),
              meta: { title: '住院病人' }
            }
          ]
        },
        {
          path: 'medical-record',
          name: 'medicalRecord',
          meta: { title: '病历管理' },
          children: [
            {
              path: 'outpatient',
              name: 'medicalRecordOutpatient',
              component: () => import('../views/Doctor/medical-record/Outpatient.vue'),
              meta: { title: '门诊病历管理' }
            },
            {
              path: 'inpatient',
              name: 'medicalRecordInpatient',
              component: () => import('../views/Doctor/medical-record/Inpatient.vue'),
              meta: { title: '住院病历管理' }
            }
          ]
        },
        {
          path: 'visit',
          name: 'visit',
          meta: { title: '出诊管理' },
          children: [
            {
              path: 'outpatient',
              name: 'visitOutpatient',
              component: () => import('../views/Doctor/visit/Outpatient.vue'),
              meta: { title: '门诊出诊' }
            },
            {
              path: 'check',
              name: 'visitCheck',
              component: () => import('../views/Doctor/visit/Check.vue'),
              meta: { title: '检查出诊' }
            },
            {
              path: 'inpatient',
              name: 'visitInpatient',
              component: () => import('../views/Doctor/visit/Inpatient.vue'),
              meta: { title: '住院出诊' }
            },
            {
              path: 'physical',
              name: 'visitPhysical',
              component: () => import('../views/Doctor/visit/Physical.vue'),
              meta: { title: '体检出诊' }
            }
          ]
        },
        {
          path: 'progress',
          name: 'progress',
          meta: { title: '病程记录' },
          children: [
            {
              path: 'outpatient',
              name: 'progressOutpatient',
              component: () => import('../views/Doctor/progress/Outpatient.vue'),
              meta: { title: '门诊病程' }
            },
            {
              path: 'inpatient',
              name: 'progressInpatient',
              component: () => import('../views/Doctor/progress/Inpatient.vue'),
              meta: { title: '住院病程' }
            }
          ]
        },
        {
          path: 'report',
          name: 'report',
          meta: { title: '报告管理' },
          children: [
            {
              path: 'outpatient',
              name: 'reportOutpatient',
              component: () => import('../views/Doctor/report/Outpatient.vue'),
              meta: { title: '门诊报告管理' }
            },
            {
              path: 'check',
              name: 'reportCheck',
              component: () => import('../views/Doctor/report/Check.vue'),
              meta: { title: '检查报告管理' }
            },
            {
              path: 'inpatient',
              name: 'reportInpatient',
              component: () => import('../views/Doctor/report/Inpatient.vue'),
              meta: { title: '住院报告管理' }
            },
            {
              path: 'physical',
              name: 'reportPhysical',
              component: () => import('../views/Doctor/report/Physical.vue'),
              meta: { title: '体检报告管理' }
            }
          ]
        },
        {
          path: 'file-make',
          name: 'FileMake',
          component: () => import('@/views/Doctor/fileMake/Layout.vue'),
          children: [
            {
              path: 'outpatient-report',
              name: 'OutpatientReport',
              component: () => import('@/views/Doctor/fileMake/OutpatientReport.vue'),
              meta: { title: '门诊报告制作' }
            },
            {
              path: 'inpatient-report',
              name: 'InpatientReport',
              component: () => import('@/views/Doctor/fileMake/InpatientReport.vue'),
              meta: { title: '住院报告制作' }
            },
            {
              path: 'check-report',
              name: 'CheckReport',
              component: () => import('@/views/Doctor/fileMake/CheckReport.vue'),
              meta: { title: '检查报告制作' }
            },
            {
              path: 'physical-report',
              name: 'PhysicalReport',
              component: () => import('@/views/Doctor/fileMake/PhysicalReport.vue'),
              meta: { title: '体检报告制作' }
            },
            {
              path: 'outpatient-progress',
              name: 'OutpatientProgress',
              component: () => import('@/views/Doctor/fileMake/OutpatientProgress.vue'),
              meta: { title: '门诊病程制作' }
            },
            {
              path: 'inpatient-progress',
              name: 'InpatientProgress',
              component: () => import('@/views/Doctor/fileMake/InpatientProgress.vue'),
              meta: { title: '住院病程制作' }
            },
            {
              path: 'outpatient-record',
              name: 'OutpatientRecord',
              component: () => import('@/views/Doctor/fileMake/OutpatientRecord.vue'),
              meta: { title: '门诊病历制作' }
            },
            {
              path: 'inpatient-record',
              name: 'InpatientRecord',
              component: () => import('@/views/Doctor/fileMake/InpatientRecord.vue'),
              meta: { title: '住院病历制作' }
            }
          ]
        }
      ]
    },

    // 管理端路由
    {
      path: '/admin',
      component: () => import('../views/Admin/AdminSystem.vue'),
      children: [
        {
          path: '',
          name: 'adminHome',
          component: () => import('../views/Admin/Home.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'doctor-accounts',
          name: 'doctorAccounts',
          component: () => import('../views/Admin/DoctorAccounts.vue'),
          meta: { title: '医生账号管理' }
        },
        {
          path: 'patient-accounts',
          name: 'patientAccounts',
          component: () => import('../views/Admin/PatientAccounts.vue'),
          meta: { title: '病人账号管理' }
        },
        {
          path: 'schedules',
          name: 'schedules',
          component: () => import('../views/Admin/Schedules.vue'),
          meta: { title: '出诊排班管理' }
        },
        {
          path: 'surgeries',
          name: 'surgeries',
          component: () => import('../views/Admin/Surgeries.vue'),
          meta: { title: '手术信息管理' }
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/Admin/Orders.vue'),
          meta: { title: '费用订单管理' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 获取token
  const token = localStorage.getItem('token')
  
  // 如果访问登录页面，直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 如果没有token，重定向到登录页面
  if (!token) {
    next('/login')
    return
  }
  
  // 获取用户角色
  const role = localStorage.getItem('role')
  
  // 检查用户是否访问其权限范围内的页面
  if (to.path.startsWith(`/${role}`)) {
    next()
  } else {
    // 如果用户访问其他角色的页面，重定向到对应角色的首页
    next(`/${role}`)
  }
})

export default router 