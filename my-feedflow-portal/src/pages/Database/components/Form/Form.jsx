import React, { Component } from 'react';
import IceContainer from '@icedesign/container';
import PropTypes from 'prop-types';
import { Input, Grid, Button, Select, Switch, Feedback } from '@icedesign/base';
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
      value: { },
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
                <h2 style={styles.formTitle}>Edit Database</h2>
                :
                <h2 style={styles.formTitle}>Add Database</h2>
              }
              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>name:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="name" required max={50} message="database nickname is required">
                    <Input size="large" placeholder="please input database nickname" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="name" />
                </Col>
              </Row>

              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>database:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="type" required max={50} message="database type is required">
                    <Select style={{ width: '100%' }} size="large" placeholder="please select database type">
                      <Select.Option value="sqlserver">SQL Server</Select.Option>
                      <Select.Option value="mysql">Mysql</Select.Option>
                    </Select>
                  </IceFormBinder>
                  <IceFormError name="type" />
                </Col>
              </Row>

              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>server:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="server" required max={50} message="database server is required">
                    <Input size="large" placeholder="ComputerID\SQL" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="server" />
                </Col>
              </Row>

              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>user:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="user" required max={50} message="database user is required">
                    <Input size="large" placeholder="please input database user" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="user" />
                </Col>
              </Row>

              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>password:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="password" required max={50} message="database password is required">
                    <Input htmlType="password" size="large" placeholder="please input database password" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="password" />
                </Col>
              </Row>

              <Row style={styles.formItem}>
                <Col xxs="6" s="3" l="3" style={styles.formLabel}>database:</Col>
                <Col s="12" l="10">
                  <IceFormBinder name="database" required max={50} message="db name is required">
                    <Input size="large" placeholder="please input db name" style={{ width: '100%' }} />
                  </IceFormBinder>
                  <IceFormError name="database" />
                </Col>
              </Row>

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
