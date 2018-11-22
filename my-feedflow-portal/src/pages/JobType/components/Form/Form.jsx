import React, { Component } from 'react';
import IceContainer from '@icedesign/container';
import PropTypes from 'prop-types';
import { Input, Grid, Button, Select, Switch, Feedback, NumberPicker, Transfer } from '@icedesign/base';
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
    jobSteps : PropTypes.array.isRequired,
    onSubmit : PropTypes.func.isRequired,
  };

  static defaultProps = {};

  constructor(props) {
    super(props);
    this.state = {
      loading : false,
      jobStep : [],
      value: {},
    };
  }

  componentDidMount = async() => {
    if(this.props.id){
      this.setState({loading : true})
      let value = await this.props.loadData()
      let jobStep = value.jobStep || []
      this.setState({loading : false, jobStep, value : value})
    }
  }

  formChange = (value) => {
    this.setState({ value });
  };

  validateAllFormField = () => {
    this.setState({loading : true})
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

  onJobStepChange = (value, data, extra) =>{
    let jobStep = this.state.jobStep
    switch (extra.direction) {
      case "left":
        let jobList = []
        jobStep.forEach(job=>{ if(extra.movedValue.indexOf(job) == -1){ jobList.push(job) }  })
        jobStep = jobList
        break;
      case "right":
        extra.movedValue.forEach(v=>{ jobStep.push(v) })
        break;
    }
    this.setState({ jobStep })
  }

  render() {
    return (
      <div className="user-form">
        <IceContainer>
          <IceFormBinderWrapper value={this.state.value} onChange={this.formChange} ref="form" >
            <div style={styles.formContent}>
              {
                this.props.id ?
                <h2 style={styles.formTitle}>Edit JobType</h2>
                :
                <h2 style={styles.formTitle}>Add JobType</h2>
              }
              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>Name:</Col>
                <Col s="15" l="15">
                  <IceFormBinder name="name" required max={50} message="jobType nickname is required">
                    <Input size="large" placeholder="please input jobType nickname" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="name" />
                </Col>
              </Row>
              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>JobStep:</Col>
                <Col s="15" l="15">
                  <IceFormBinder name="jobStep" required message="jobStep is required">
                    <Transfer dataSource={this.props.jobSteps} value={this.state.jobStep} onChange={this.onJobStepChange} listStyle={{ width: "250px" }}/>
                  </IceFormBinder>
                  <IceFormError name="jobStep" />
                </Col>
              </Row>
            </div>
          </IceFormBinderWrapper>

          <Row style={{ marginTop: 20 }}>
            <Col offset="5">
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
