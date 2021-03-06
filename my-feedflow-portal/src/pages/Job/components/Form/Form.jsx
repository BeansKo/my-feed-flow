import React, { Component } from 'react';
import IceContainer from '@icedesign/container';
import PropTypes from 'prop-types';
import { Input, Grid, Button, Select, Transfer, Switch, Feedback, NumberPicker } from '@icedesign/base';
import Monaco from 'react-monaco-editor';
import {
  FormBinderWrapper as IceFormBinderWrapper,
  FormBinder as IceFormBinder,
  FormError as IceFormError,
} from '@icedesign/form-binder';

const { Row, Col } = Grid;
export default class Form extends Component {
    static displayName = 'Form';

    static propTypes = {
        id : PropTypes.string,
        loadData : PropTypes.func,
        loadStepConfig : PropTypes.func.isRequired,
        checkScheduled : PropTypes.func.isRequired,
        database : PropTypes.array.isRequired,
        jobSteps : PropTypes.array.isRequired,
        // url: PropTypes.array.isRequired,
        email : PropTypes.array.isRequired,
        jobType : PropTypes.array.isRequired,
        onSubmit : PropTypes.func.isRequired,
    };

    static defaultProps = {};

    constructor(props) {
        super(props);
        this.state = {
            loading : false,
            jobSteps : [],
            jobConfig : [],
            value: {
                config : {}
            },
            monacoReady: false
        };
    }

    componentDidMount = async() => {
        if(this.props.id){
            this.setState({loading : true})
            let value = await this.props.loadData()
            let jobStep = value.jobStep || []
            let jobConfig = await this.props.loadStepConfig(value.jobType)
            this.setState({loading : false, jobStep: jobStep, jobConfig: jobConfig, value : value})
        }
    }

    formChange = (value) => {
        this.setState({ value });
    };

    fillDefaultValue = () => {
        this.setState((prevState, props) => {
          let value = prevState.value
          if(!value.logLevel) {
            value.logLevel = "INFO"
          }
          if(!value.config) {
            value.config = {}
          }
          if(this.state.jobConfig && this.state.jobConfig.length > 0) {
            this.state.jobConfig.map(config=>{
              if(!value.config[config.name] && config.defaultValue) {
                value.config[config.name] = config.defaultValue
              }
            })
          }
          return { value: value }
        });
    }

    validateAllFormField = () => {
        this.setState({loading : true})
        this.fillDefaultValue()
        this.refs.form.validateAll(async(errors, values) => {
          if (errors) { this.setState({loading : false}); return; }
          values.jobStep = this.state.jobStep
          let flag = await this.props.onSubmit(values)
          if(flag){
            Feedback.toast.success('Success');
          }else{
            Feedback.toast.error('Fail');
          }
          this.setState({loading : false})
        });
    };

    checkScheduled = async(rule, values, callback, stateValues) =>{
        if(!values){
          callback('please input job scheduled');
        } else {
          let flag = false
          try { flag = await this.props.checkScheduled(values) } catch (error) { }
          if(flag){ callback(flag);  }else{ callback() }
        }
    }

    onJobTypeChange = async(value) => {
        if(!value){
          this.setState({ jobConfig : [] })
        } else {
          let configs = await this.props.loadStepConfig(value)
          this.setState({ jobConfig : configs })
        }
    }

    renderStepConfig = () => {
        let stepConfig = ""
        if(this.state.jobConfig && this.state.jobConfig.length >0) {
            stepConfig = this.state.jobConfig.map(config =>{
                switch(config.type){
                    case "TEXT":
                        return this.renderText(config);
                    case "DATABASE":
                        return this.renderDatabase(config);
                    case "SQL":
                        return this.renderSql(config);
                    default:
                        return ""
                }
            })
        }
        return <div>{stepConfig}</div>
    }

    renderTextarea(config) {
        return (
          <Row key={config.name} style={styles.formItem}>
            <Col xxs="6" s="3" l="3" style={styles.formLabel}>{config.label}</Col>
            <Col s="15" l="15">
              <IceFormBinder name={"config." + config.name} required={config.required} message={config.label + " is required"}>
                <Input size="large" defaultValue={config.defaultValue} multiple placeholder={"please input " + config.label} style={{ width: '100%' }} />
              </IceFormBinder>
              <IceFormError name={"config." + config.name} />
            </Col>
          </Row>
        )
    }

    renderSql(config) {
        return (
          <Row key={config.name} style={styles.formItem}>
            <Col xxs="6" s="3" l="3" style={styles.formLabel}>{config.label}</Col>
            <Col s="15" l="15" style={{border:'1px solid #dcdee3'}}>
              <IceFormBinder name={"config." + config.name} required={config.required} message={config.label + " is required"}>
                <Monaco
                  height="300"
                  language="sql"
                  options={monacoOptions}
                  defaultValue={config.defaultValue} />
              </IceFormBinder>
              <IceFormError name={"config." + config.name} />
            </Col>
          </Row>
        )
    }

    renderDatabase(config) {
        return (
            <Row key={config.name} style={styles.formItem}>
              <Col xxs="6" s="3" l="3" style={styles.formLabel}>{config.label}</Col>
              <Col s="15" l="15">
                <IceFormBinder name={"config." + config.name} required={config.required} message={config.label + " is required"}>
                  <Select size="large" placeholder={"please select " + config.label} style={{ width: '100%' }}>
                    {
                      this.props.database.map(db=>{
                        return <Select.Option key={db.id} value={db.id}>{db.name}</Select.Option>
                      })
                    }
                  </Select>
                </IceFormBinder>
                <IceFormError name={"config." + config.name} />
              </Col>
            </Row>
        )
    }

    renderText(config) {
        return (
          <Row key={config.name} style={styles.formItem}>
            <Col xxs="6" s="3" l="3" style={styles.formLabel}>{config.label}</Col>
            <Col s="15" l="15">
              <IceFormBinder name={"config." + config.name} required={config.required} message={config.label + " is required"}>
                <Input size="large" defaultValue={config.defaultValue} placeholder={"please input " + config.label} style={{ width: '100%' }} />
              </IceFormBinder>
              <IceFormError name={"config." + config.name} />
            </Col>
          </Row>
        )
    }

    render() {
        let stepConfig = this.renderStepConfig();
        return (
            <div className="user-form">
                <IceContainer>
                    <IceFormBinderWrapper value={this.state.value} onChange={this.formChange} ref="form" >
                        <div style={styles.formContent}>
                        {
                            this.props.id ?
                            <h2 style={styles.formTitle}>Edit Job</h2>
                            :
                            <h2 style={styles.formTitle}>Add Job</h2>
                        }
                        <Row style={styles.formItem}>
                            <Col xxs="6" s="3" l="3" style={styles.formLabel}>Name</Col>
                            <Col s="15" l="15">
                            <IceFormBinder name="name" required max={50} message="job name is required">
                                <Input size="large" placeholder="please input job name" style={{ width: '100%' }} />
                            </IceFormBinder>
                            <IceFormError name="name" />
                            </Col>
                        </Row>
                        <Row style={styles.formItem}>
                            <Col xxs="6" s="3" l="3" style={styles.formLabel}>Scheduled</Col>
                            <Col s="15" l="15">
                            <IceFormBinder name="scheduled" required max={50}
                                validator={(rule, values, callback) =>
                                this.checkScheduled(rule,values,callback,this.state.value)
                                }>
                                <Input size="large" placeholder="0 0 0 * * ?" style={{ width: '100%' }} />
                            </IceFormBinder>
                            <IceFormError name="scheduled" />
                            </Col>
                        </Row>
                        <Row style={styles.formItem}>
                            <Col xxs="6" s="3" l="3" style={styles.formLabel}>Log Level</Col>
                            <Col s="15" l="15">
                            <IceFormBinder name="logLevel" required max={50} message="job log level is required">
                                <Select style={{ width: '100%' }} size="large" placeholder="please select job log level" defaultValue="INFO">
                                <Select.Option value="ERROR">ERROR</Select.Option>
                                <Select.Option value="WARN">WARN</Select.Option>
                                <Select.Option value="INFO">INFO</Select.Option>
                                <Select.Option value="DEBUG">DEBUG</Select.Option>
                                </Select>
                            </IceFormBinder>
                            <IceFormError name="logLevel" />
                            </Col>
                        </Row>
                        <Row style={styles.formItem}>
                            <Col xxs="6" s="3" l="3" style={styles.formLabel}>Log Email</Col>
                            <Col s="15" l="15">
                            <IceFormBinder name="logEmail" required message="job log email is required">
                                <Select size="large" placeholder="please select log email" style={{ width: '100%' }}>
                                {
                                    this.props.email.map(email=>{
                                    return <Select.Option key={email.id} value={email.id}>{email.name}</Select.Option>
                                    })
                                }
                                </Select>
                            </IceFormBinder>
                            <IceFormError name="logEmail" />
                            </Col>
                        </Row>
                        <Row style={styles.formItem}>
                            <Col xxs="6" s="3" l="3" style={styles.formLabel}>JobType</Col>
                            <Col s="15" l="15">
                            <IceFormBinder name="jobType" required message="jobType is required">
                                <Select size="large" placeholder="please select jobType" style={{ width: '100%' }} onChange={this.onJobTypeChange}>
                                {
                                    this.props.jobType.map(jobType=>{
                                    return <Select.Option key={jobType.id} value={jobType.id}>{jobType.name}</Select.Option>
                                    })
                                }
                                </Select>
                            </IceFormBinder>
                            <IceFormError name="jobType" />
                            </Col>
                        </Row>
                        <Row style={styles.formItem}>
                            <Col xxs="6" s="3" l="3" style={styles.formLabel}>Enable</Col>
                            <Col s="15" l="15">
                            <IceFormBinder name="status">
                                <Switch checked={this.state.value.status}/>
                            </IceFormBinder>
                            <IceFormError name="status" />
                            </Col>
                        </Row>
                        {stepConfig}
                        </div>
                    </IceFormBinderWrapper>

                    <Row style={{ marginTop: 20 }}>
                        <Col offset="3">
                        <Button type="primary" loading={this.state.loading} disabled={this.state.loading} onClick={this.validateAllFormField}> 提 交 </Button>
                        </Col>
                    </Row>
                </IceContainer>
            </div>
        );
    }

}

const styles = {
    formContent: {
      width: '100%',
      position: 'relative',
    },
    formItem: {
      marginBottom: 25,
    },
    formLabel: {
      height: '32px',
      lineHeight: '32px',
      textAlign: 'right',
      paddingRight : '5px',
    },
    formTitle: {
      margin: '0 0 20px',
      paddingBottom: '10px',
      borderBottom: '1px solid #eee',
    },
  };

const monacoOptions = {
    selectOnLineNumbers: true,
    automaticLayout: true,
    theme: 'vs', //vs-dark
    minimap: { enabled: false },
};
