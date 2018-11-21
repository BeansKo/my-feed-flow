import React, { Component } from 'react';
import IceContainer from '@icedesign/container';
import PropTypes from 'prop-types';
import { Input, Grid, Button, Select, Switch, Feedback,NumberPicker } from '@icedesign/base';
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
    onSubmit : PropTypes.func.isRequired,
  };

  static defaultProps = {};

  constructor(props) {
    super(props);
    this.state = {
      loading : false,
      value: {},
    };
  }

  componentDidMount = async() => {
    if(this.props.id){
      this.setState({loading : true})
      let value = await this.props.loadData()
      this.setState({loading : false, value : value})
    }
  }

  formChange = (value) => {
    this.setState({ value });
  };

  validateAllFormField = () => {
    this.setState({loading : true})
    this.refs.form.validateAll(async(errors, values) => {
      if (errors) { this.setState({loading : false}); return; }
      let flag = await this.props.onSubmit(values)
      if(flag){
        Feedback.toast.success('Success');
      }else{
        Feedback.toast.error('Fail');
      }
      this.setState({loading : false})
    });
  };

  render() {
    return (
      <div className="user-form">
        <IceContainer>
          <IceFormBinderWrapper value={this.state.value} onChange={this.formChange} ref="form" >
            <div style={styles.formContent}>
              {
                this.props.id ?
                <h2 style={styles.formTitle}>Edit Email</h2>
                :
                <h2 style={styles.formTitle}>Add Email</h2>
              }
              <Row style={styles.formItem}>
                <Col xxs="6" s="5" l="5" style={styles.formLabel}>Name:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="name" required max={50} message="email nickname is required">
                    <Input size="large" placeholder="please input email nickname" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="name" />
                </Col>
              </Row>
              <Row style={styles.formItem}>
                <Col xxs="6" s="5" l="5" style={styles.formLabel}>From:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="from" required max={500} message="from address is required">
                    <Input size="large" placeholder="please input from address, split with ','" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="from" />
                </Col>
              </Row>
              <Row style={styles.formItem}>
                <Col xxs="6" s="5" l="5" style={styles.formLabel}>To:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="to" required max={500} message="to address is required">
                    <Input size="large" placeholder="please input to address, split with ','" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="to" />
                </Col>
              </Row>
              <Row style={styles.formItem}>
                <Col xxs="6" s="5" l="5" style={styles.formLabel}>CC:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="cc" max={500}>
                    <Input size="large" placeholder="please input cc address, split with ','" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="cc" />
                </Col>
              </Row>
              <Row style={styles.formItem}>
                <Col xxs="6" s="5" l="5" style={styles.formLabel}>Subject:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="subject" required max={50} message="subject is required">
                    <Input size="large" placeholder="please input subject" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="subject" />
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